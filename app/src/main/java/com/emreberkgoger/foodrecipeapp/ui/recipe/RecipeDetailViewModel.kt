package com.emreberkgoger.foodrecipeapp.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emreberkgoger.foodrecipeapp.data.dto.response.IngredientDto
import com.emreberkgoger.foodrecipeapp.data.dto.response.RecipeDetailDto
import com.emreberkgoger.foodrecipeapp.data.repository.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    private val _recipeDetail = MutableStateFlow<RecipeDetailDto?>(null)
    val recipeDetail: StateFlow<RecipeDetailDto?> = _recipeDetail

    private val _missingIngredients = MutableStateFlow<List<IngredientDto>>(emptyList())
    val missingIngredients: StateFlow<List<IngredientDto>> = _missingIngredients

    fun loadRecipeDetail(recipeId: Long) {
        viewModelScope.launch {
            val detailResponse = recipeRepository.getRecipeById(recipeId)
            if (detailResponse.isSuccessful) {
                _recipeDetail.value = detailResponse.body()
            }
            val missingResponse = recipeRepository.getMissingIngredients(recipeId)
            if (missingResponse.isSuccessful) {
                _missingIngredients.value = missingResponse.body() ?: emptyList()
            }
        }
    }
} 