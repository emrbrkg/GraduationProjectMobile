package com.emreberkgoger.foodrecipeapp.data.dto.response

import com.emreberkgoger.foodrecipeapp.data.dto.response.NutritionDto

data class IngredientDto(
    val id: Long,
    val name: String,
    val amount: Float?,
    val unit: String?,
    val nutrition: NutritionDto?
)