package com.emreberkgoger.foodrecipeapp.data.dto.request

data class UserUpdateDto(
    val userName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val dietTypes: List<String>? = null
)