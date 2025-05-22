package com.emreberkgoger.foodrecipeapp.data.model.recipe

data class Recipe(
    val id: Long,
    val title: String,
    val description: String?,
    val ingredients: List<String>,
    val instructions: String?,
    val imageUrl: String?,
    val dietTypes: List<String> = emptyList()
)