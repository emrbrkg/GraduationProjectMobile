package com.emreberkgoger.foodrecipeapp.data.repository.ingredient

import com.emreberkgoger.foodrecipeapp.data.api.ApiService
import com.emreberkgoger.foodrecipeapp.data.dto.request.UserIngredientAddDto
import com.emreberkgoger.foodrecipeapp.data.dto.response.IngredientDto
import com.emreberkgoger.foodrecipeapp.data.model.ingredient.Ingredient
import javax.inject.Inject

class IngredientRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllIngredients() = apiService.getAllIngredients()
    suspend fun getIngredientById(id: Long) = apiService.getIngredientById(id)
    suspend fun searchIngredients(query: String) = apiService.searchIngredients(query)
    suspend fun saveIngredient(ingredient: IngredientDto) = apiService.saveIngredient(ingredient)
    suspend fun updateIngredient(id: Long, ingredient: IngredientDto) = apiService.updateIngredient(id, ingredient)
    suspend fun deleteIngredient(id: Long) = apiService.deleteIngredient(id)
}