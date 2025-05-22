package com.emreberkgoger.foodrecipeapp.data.repository.nutrition

import com.emreberkgoger.foodrecipeapp.data.api.ApiService
import com.emreberkgoger.foodrecipeapp.data.dto.request.NutritionAddDto
import javax.inject.Inject

class NutritionRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getNutritionForIngredient(ingredientId: Long) = apiService.getNutritionForIngredient(ingredientId)
    suspend fun getNutritionForRecipe(recipeId: Long) = apiService.getNutritionForRecipe(recipeId)
    suspend fun saveNutritionForIngredient(ingredientId: Long, request: NutritionAddDto) =
        apiService.saveNutritionForIngredient(ingredientId, request)
    suspend fun deleteNutrition(nutritionId: Long) = apiService.deleteNutrition(nutritionId)
}