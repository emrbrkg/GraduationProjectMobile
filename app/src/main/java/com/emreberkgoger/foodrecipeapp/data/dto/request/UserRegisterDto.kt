package com.emreberkgoger.foodrecipeapp.data.dto.request

data class UserRegisterDto(
    val email: String,
    val password: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val dietTypes: List<String>
)