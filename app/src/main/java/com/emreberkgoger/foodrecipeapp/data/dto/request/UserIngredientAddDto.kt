package com.emreberkgoger.foodrecipeapp.data.dto.request

data class UserIngredientAddDto(
    val ingredientId: Long,
    val amount: Float?,
    val unit: String?
)