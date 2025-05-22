package com.emreberkgoger.foodrecipeapp.data.dto.response

data class JwtResponseDto(
    val token: String,
    val type: String = "Bearer",
    val id: Long,
    val email: String
)