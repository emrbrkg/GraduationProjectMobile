package com.emreberkgoger.foodrecipeapp.data.model.user

data class User(
    val id: Long,
    val email: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val createdDate: String,
    val chosenDietTypes: List<String> = emptyList()
)