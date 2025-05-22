package com.emreberkgoger.foodrecipeapp.data.model.nutrition

data class Nutrition(
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val energyUnit: String?,
    val macroNutrientUnit: String?,
    val portionSize: String?,
    val referenceAmount: Float?,
    val referenceUnit: String?,
    val additionalValues: Map<String, Float>?
)