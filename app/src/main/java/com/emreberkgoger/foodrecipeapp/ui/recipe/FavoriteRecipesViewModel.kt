package com.emreberkgoger.foodrecipeapp.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emreberkgoger.foodrecipeapp.data.model.recipe.Recipe
import com.emreberkgoger.foodrecipeapp.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val favoriteRecipes: StateFlow<List<Recipe>> = _favoriteRecipes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchFavoriteRecipes() {
        viewModelScope.launch {
            val response = userRepository.getUserFavoriteRecipes()
            if (response.isSuccessful) {
                val dtoList = response.body() ?: emptyList()
                _favoriteRecipes.value = dtoList.map { dto ->
                    Recipe(
                        id = dto.id,
                        title = dto.title,
                        description = null,
                        ingredients = emptyList(),
                        instructions = null,
                        imageUrl = dto.imageUrl,
                        dietTypes = dto.supportedDietTypes ?: emptyList(),
                        isFavorite = dto.isFavorite
                    )
                }
            } else {
                _errorMessage.value = "Favori tarifler alınamadı: ${response.message()}"
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun addFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val response = userRepository.addFavoriteRecipe(recipe.id)
            if (response.isSuccessful) {
                _favoriteRecipes.value = _favoriteRecipes.value.map {
                    if (it.id == recipe.id) it.copy(isFavorite = true) else it
                }
            } else {
                _errorMessage.value = "Favoriye eklenemedi: ${response.message()}"
            }
        }
    }

    fun removeFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val response = userRepository.removeFavoriteRecipe(recipe.id)
            if (response.isSuccessful) {
                _favoriteRecipes.value = _favoriteRecipes.value.filterNot { it.id == recipe.id }
            } else {
                _errorMessage.value = "Favoriden çıkarılamadı: ${response.message()}"
            }
        }
    }
} 