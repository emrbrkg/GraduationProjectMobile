package com.emreberkgoger.foodrecipeapp.data.repository.OCR

import com.emreberkgoger.foodrecipeapp.data.api.ApiService
import okhttp3.MultipartBody
import javax.inject.Inject

class OCRRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun processReceiptImage(file: MultipartBody.Part) = apiService.processReceiptImage(file)
}