package com.emreberkgoger.foodrecipeapp.data.dto.request

data class RecipeSearchDto(
    val query: String?,
    val dietTypes: List<String>?,
    val ingredients: List<String>?,
    val useUserIngredients: Boolean?,
    val maxReadyTime: Int?,
    val limit: Int? = 1
)