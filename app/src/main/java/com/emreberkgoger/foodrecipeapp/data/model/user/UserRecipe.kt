package com.emreberkgoger.foodrecipeapp.data.model.user

data class UserRecipe(
    val id: Long,           // Favori kaydının ID'si
    val userId: Long,       // Kullanıcı ID'si
    val recipeId: Long,     // Tarif ID'si
    val createdAt: String?  // Favoriye eklenme zamanı (opsiyonel)
)