package com.emreberkgoger.foodrecipeapp.data.dto.response

data class RecipeDetailDto(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val instructions: String?,
    val sourceUrl: String?,
    val supportedDietTypes: List<String>?,
    val ingredients: List<IngredientDto>?,
    val nutrition: NutritionDto?,
    val missingIngredients: List<String>?,
    val isFavorite: Boolean?
)