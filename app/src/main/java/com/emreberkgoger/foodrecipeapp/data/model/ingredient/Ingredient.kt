package com.emreberkgoger.foodrecipeapp.data.model.ingredient

data class Ingredient(
    val id: Long,
    val name: String,
    val type: String? = null
)