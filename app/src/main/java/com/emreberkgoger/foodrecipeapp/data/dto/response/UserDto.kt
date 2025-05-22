package com.emreberkgoger.foodrecipeapp.data.dto.response

data class UserDto(
    val id: Long,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: String // ISO tarih stringi
)