package com.emreberkgoger.foodrecipeapp.data.dto.response

data class OCRResultDto(
    val extractedProducts: List<String>,
    val success: Boolean,
    val message: String?
)