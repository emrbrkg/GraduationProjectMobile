package com.emreberkgoger.foodrecipeapp.data.dto.response

import java.io.Serializable

data class UserIngredientResponseDto(
    val id: Long,
    val ingredientId: Long,
    val ingredientName: String?,
    val quantity: Float?,
    val unit: String?,
    val expiryDate: String?
) : Serializable