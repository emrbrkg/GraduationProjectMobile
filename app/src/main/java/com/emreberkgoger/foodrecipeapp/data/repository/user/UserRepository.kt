package com.emreberkgoger.foodrecipeapp.data.repository.user

import com.emreberkgoger.foodrecipeapp.data.api.ApiService
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserIngredientAddDto
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserLoginDto
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserRegisterDto
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserUpdateDto
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun register(request: UserRegisterDto) = apiService.register(request)
    suspend fun login(request: UserLoginDto) = apiService.login(request)
    suspend fun getCurrentUserProfile() = apiService.getCurrentUserProfile()
    suspend fun updateUserProfile(request: UserUpdateDto) = apiService.updateUserProfile(request)
    suspend fun updateDietPreferences(dietTypes: List<String>) = apiService.updateDietPreferences(dietTypes)
    suspend fun getUserIngredients() = apiService.getUserIngredients()
    suspend fun addUserIngredient(request: UserIngredientAddDto) = apiService.addUserIngredient(request)
    suspend fun addUserIngredients(requests: List<UserIngredientAddDto>) = apiService.addUserIngredients(requests)
    suspend fun updateUserIngredient(id: Long, request: UserIngredientAddDto) = apiService.updateUserIngredient(id, request)
    suspend fun removeUserIngredient(id: Long) = apiService.removeUserIngredient(id)
    suspend fun getUserFavoriteRecipes() = apiService.getUserFavoriteRecipes()
    suspend fun addFavoriteRecipe(recipeId: Long) = apiService.addFavoriteRecipe(recipeId)
    suspend fun removeFavoriteRecipe(recipeId: Long) = apiService.removeFavoriteRecipe(recipeId)
    suspend fun deleteUser(userId: Long) = apiService.deleteUser(userId)
}