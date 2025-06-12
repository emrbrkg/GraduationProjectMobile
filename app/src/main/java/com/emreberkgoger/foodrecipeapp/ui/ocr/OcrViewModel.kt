package com.emreberkgoger.foodrecipeapp.ui.ocr

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emreberkgoger.foodrecipeapp.data.dto.response.OCRResultDto
import com.emreberkgoger.foodrecipeapp.data.repository.OCR.OCRRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import com.emreberkgoger.foodrecipeapp.data.repository.ingredient.UserIngredientRepository
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserIngredientAddDto

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val ocrRepository: OCRRepository,
    private val userIngredientRepository: UserIngredientRepository
) : ViewModel() {
    private val _ocrResult = MutableLiveData<OCRResultDto?>()
    val ocrResult: LiveData<OCRResultDto?> = _ocrResult
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    private var lastOcrProducts: List<String>? = null

    fun processImageUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val file = uriToFile(context, uri)
                processImageFile(context, file)
            } catch (e: Exception) {
                setError("Görsel işlenemedi: ${e.localizedMessage}")
            }
        }
    }

    fun processImageFile(context: Context, file: File) {
        viewModelScope.launch {
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                Log.d("OCR_REQUEST", "Gönderilen dosya: ${file.name}")
                val response = ocrRepository.processReceiptImage(body)
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("OCR_RESPONSE", result.toString())
                    _ocrResult.value = result
                    lastOcrProducts = result?.extractedProducts
                    _successMessage.value = result?.message ?: "Malzemeler başarıyla tarandı."
                } else {
                    setError("Sunucu hatası: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                setError("İşlem sırasında hata: ${e.localizedMessage}")
            }
        }
    }

    fun confirmOcrResult() {
        val products = lastOcrProducts ?: _ocrResult.value?.extractedProducts ?: emptyList()
        if (products.isEmpty()) {
            setError("Eklenebilecek malzeme yok.")
            return
        }
        viewModelScope.launch {
            var successCount = 0
            var failCount = 0
            val failNames = mutableListOf<String>()
            for (name in products) {
                try {
                    val dto = UserIngredientAddDto(
                        ingredientId = null,
                        ingredientName = name,
                        quantity = 100f,
                        unit = "g",
                        expiryDate = null
                    )
                    val response = userIngredientRepository.addUserIngredient(dto)
                    if (response.isSuccessful) {
                        successCount++
                    } else {
                        failCount++
                        failNames.add(name)
                        Log.e("OCR_ADD_INGREDIENT", "${name} eklenemedi: ${response.message()}")
                    }
                } catch (e: Exception) {
                    failCount++
                    failNames.add(name)
                    Log.e("OCR_ADD_INGREDIENT", "${name} eklenemedi: ${e.localizedMessage}")
                }
            }
            if (successCount > 0) {
                _successMessage.value = "$successCount malzeme listenize eklendi."
            }
            if (failCount > 0) {
                setError("${failCount} malzeme eklenemedi: ${failNames.joinToString(", ")}")
            }
        }
    }

    fun setError(msg: String) {
        _errorMessage.value = msg
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        // Gerçek dosya yoluna çevirme işlemi (kopyalama gerekebilir)
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("ocr_image", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
} 