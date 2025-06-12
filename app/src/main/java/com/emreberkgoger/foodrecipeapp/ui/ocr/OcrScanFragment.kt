package com.emreberkgoger.foodrecipeapp.ui.ocr

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emreberkgoger.foodrecipeapp.R
import com.emreberkgoger.foodrecipeapp.data.dto.response.OCRResultDto
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ImageButton
import android.view.Gravity

class OcrScanFragment : Fragment() {
    private val viewModel: OcrViewModel by activityViewModels()
    private var currentPhotoPath: String? = null
    private lateinit var tvOcrResult: TextView
    private lateinit var btnSelectImage: Button
    private lateinit var btnConfirmOcr: Button

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.processImageUri(requireContext(), it) }
    }
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success && currentPhotoPath != null) {
            val file = File(currentPhotoPath!!)
            viewModel.processImageFile(requireContext(), file)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ocr_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvOcrResult = view.findViewById(R.id.tvOcrResult)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnConfirmOcr = view.findViewById(R.id.btnConfirmOcr)

        btnSelectImage.setOnClickListener {
            showImagePickerDialog()
        }
        btnConfirmOcr.setOnClickListener {
            viewModel.confirmOcrResult()
        }

        viewModel.ocrResult.observe(viewLifecycleOwner) { result ->
            val products = result?.extractedProducts ?: emptyList()
            if (products.isEmpty()) {
                tvOcrResult.text = "Taranan malzemeler burada görünecek."
            } else {
                tvOcrResult.text = products.joinToString("\n")
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.e("OCR_ERROR", it)
            }
        }
        viewModel.successMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.d("OCR_SUCCESS", it)
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Kamera ile Çek", "Galeriden Seç")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Fotoğraf Kaynağı Seç")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> pickImageLauncher.launch("image/*")
                }
            }.show()
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                viewModel.setError("Kamera dosyası oluşturulamadı.")
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(), "${requireContext().packageName}.fileprovider", it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureLauncher.launch(photoURI)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(null)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply { currentPhotoPath = absolutePath }
    }
} 