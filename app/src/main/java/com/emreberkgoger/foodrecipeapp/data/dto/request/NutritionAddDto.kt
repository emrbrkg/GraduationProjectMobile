package com.emreberkgoger.foodrecipeapp.data.dto.request

data class NutritionAddDto(
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val referenceAmount: Float = 100f,
    val referenceUnit: String = "g",
    val portionSize: String? = null,
    val energyUnit: String = "kcal",
    val macroNutrientUnit: String = "g",
    val additionalValues: Map<String, Float>? = null
)