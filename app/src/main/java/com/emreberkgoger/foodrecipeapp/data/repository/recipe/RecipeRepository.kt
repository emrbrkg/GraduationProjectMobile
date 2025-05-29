package com.emreberkgoger.foodrecipeapp.data.repository.recipe

import com.emreberkgoger.foodrecipeapp.data.api.ApiService
import com.emreberkgoger.foodrecipeapp.data.dto.request.RecipeSearchDto
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun searchRecipes(request: RecipeSearchDto) = apiService.searchRecipes(request)
    suspend fun getRecipeById(id: Long) = apiService.getRecipeById(id)
    suspend fun getRecipesByIngredients(ingredients: List<String>, limit: Int = 1) =
        apiService.getRecipesByIngredients(ingredients, limit)
    suspend fun getRecipesByUserIngredients(limit: Int = 1) = apiService.getRecipesByUserIngredients(limit)
    suspend fun getMissingIngredients(recipeId: Long) = apiService.getMissingIngredients(recipeId)
    suspend fun saveRecipeFromApi(apiRecipeId: Long) = apiService.saveRecipeFromApi(apiRecipeId)
}