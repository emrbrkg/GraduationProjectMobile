package com.emreberkgoger.foodrecipeapp.ui.ocr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.emreberkgoger.foodrecipeapp.R

class OcrResultFragment : Fragment() {
    private val viewModel: OcrViewModel by activityViewModels()
    private lateinit var tvOcrResult: TextView
    private lateinit var btnConfirm: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ocr_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvOcrResult = view.findViewById(R.id.tvOcrResult)
        btnConfirm = view.findViewById(R.id.btnConfirmOcr)

        viewModel.ocrResult.observe(viewLifecycleOwner) { result ->
            tvOcrResult.text = result?.extractedProducts?.joinToString("\n") ?: "Malzeme bulunamadı."
        }
        btnConfirm.setOnClickListener {
            viewModel.confirmOcrResult()
        }
        viewModel.successMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
} 