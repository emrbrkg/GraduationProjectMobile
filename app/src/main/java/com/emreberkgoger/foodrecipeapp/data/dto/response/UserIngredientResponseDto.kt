package com.emreberkgoger.foodrecipeapp.data.dto.response

data class UserIngredientResponseDto(
    val id: Long,
    val userId: Long,
    val ingredientId: Long,
    val amount: Float?,
    val unit: String?
)