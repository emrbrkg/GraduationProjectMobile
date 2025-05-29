package com.emreberkgoger.foodrecipeapp.data.dto.response

import com.google.gson.annotations.SerializedName

data class ProfileDto(
    val id: Long,
    val userName: String,
    val email: String,
    val createdAt: String?,
    @SerializedName("dietTypes")
    val dietTypes: List<String>
)