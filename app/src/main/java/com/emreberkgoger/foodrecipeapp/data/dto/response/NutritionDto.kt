package com.emreberkgoger.foodrecipeapp.data.dto.response

data class NutritionDto(
    val id: Long?,
    val ingredientId: Long?,
    val ingredientName: String?,
    val calories: Float?,
    val protein: Float?,
    val fat: Float?,
    val carbs: Float?,
    val portionSize: String?,
    val energyUnit: String?,
    val macroNutrientUnit: String?,
    val referenceAmount: Float?,
    val referenceUnit: String?,
    val additionalValues: Map<String, Float>?
)