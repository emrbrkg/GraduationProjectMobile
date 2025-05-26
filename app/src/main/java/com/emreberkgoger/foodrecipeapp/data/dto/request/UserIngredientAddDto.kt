package com.emreberkgoger.foodrecipeapp.data.dto.request

data class UserIngredientAddDto(
    val ingredientId: Long?,
    val ingredientName: String?,
    val quantity: Float?,
    val unit: String?,
    val expiryDate: String?
)