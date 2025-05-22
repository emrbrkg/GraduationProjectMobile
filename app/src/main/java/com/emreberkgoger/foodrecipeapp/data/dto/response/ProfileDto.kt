package com.emreberkgoger.foodrecipeapp.data.dto.response

data class ProfileDto(
    val id: Long,
    val userName: String,
    val email: String,
    val createdAt: String,
    val dietTypes: List<String>
)