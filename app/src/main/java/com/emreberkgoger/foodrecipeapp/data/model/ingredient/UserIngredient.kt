package com.emreberkgoger.foodrecipeapp.data.model.ingredient

data class UserIngredient(
    val id: Long,
    val userId: Long,
    val ingredientId: Long,
    val amount: Float?,         // Kullanıcının sahip olduğu miktar (opsiyonel)
    val unit: String?           // Birim (ör: "g", "adet", "ml" vs.)
)