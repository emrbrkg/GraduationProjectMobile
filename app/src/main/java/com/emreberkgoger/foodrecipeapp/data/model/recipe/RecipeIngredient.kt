package com.emreberkgoger.foodrecipeapp.data.model.recipe

data class RecipeIngredient(
    val id: Long,
    val recipeId: Long,
    val ingredientId: Long,
    val amount: Float?,         // Tarif içindeki miktar (opsiyonel)
    val unit: String?           // Birim (ör: "g", "adet", "ml" vs.)
)