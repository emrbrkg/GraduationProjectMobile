package com.emreberkgoger.foodrecipeapp.data.model.recipe

data class RecipeNutrition(
    val recipeId: Long,
    val nutrition: com.emreberkgoger.foodrecipeapp.data.model.nutrition.Nutrition
)