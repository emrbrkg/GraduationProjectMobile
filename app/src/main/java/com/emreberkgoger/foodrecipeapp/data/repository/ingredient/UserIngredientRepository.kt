package com.emreberkgoger.foodrecipeapp.data.repository.ingredient

import com.emreberkgoger.foodrecipeapp.data.api.ApiService
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserIngredientAddDto
import com.emreberkgoger.foodrecipeapp.data.model.ingredient.UserIngredient
import javax.inject.Inject

class UserIngredientRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserIngredients() = apiService.getUserIngredients()
    suspend fun addUserIngredient(request: UserIngredientAddDto) = apiService.addUserIngredient(request)
    suspend fun addUserIngredients(requests: List<UserIngredientAddDto>) = apiService.addUserIngredients(requests)
    suspend fun updateUserIngredient(id: Long, request: UserIngredientAddDto) = apiService.updateUserIngredient(id, request)
    suspend fun removeUserIngredient(id: Long) = apiService.removeUserIngredient(id)
}