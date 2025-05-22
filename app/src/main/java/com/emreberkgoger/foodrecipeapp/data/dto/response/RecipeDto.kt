package com.emreberkgoger.foodrecipeapp.data.dto.response

data class RecipeDto(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val supportedDietTypes: List<String>?,
    val isFavorite: Boolean?
)