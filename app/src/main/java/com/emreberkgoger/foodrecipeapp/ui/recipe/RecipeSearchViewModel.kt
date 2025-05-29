package com.emreberkgoger.foodrecipeapp.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emreberkgoger.foodrecipeapp.data.model.recipe.Recipe
import com.emreberkgoger.foodrecipeapp.data.repository.recipe.RecipeRepository
import com.emreberkgoger.foodrecipeapp.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeSearchViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _userDietTypes = MutableLiveData<List<String>>()
    val userDietTypes: LiveData<List<String>> = _userDietTypes

    private val _navigateToDetail = MutableLiveData<Recipe?>(null)
    val navigateToDetail: LiveData<Recipe?> = _navigateToDetail

    private var currentPage = 0
    private var lastQuery: String = ""
    private var lastSearchType: String = "recipe"
    private var lastDietTypes: List<String> = emptyList()
    private var lastOwnIngredients: Boolean = false
    private var lastFavorites: Boolean = false

    init {
        fetchUserDietTypes()
    }

    fun fetchUserDietTypes() {
        viewModelScope.launch {
            val response = userRepository.getCurrentUserProfile()
            if (response.isSuccessful) {
                _userDietTypes.value = response.body()?.dietTypes ?: emptyList()
            }
        }
    }

    fun searchRecipes(
        query: String,
        searchType: String,
        dietTypes: List<String>,
        onlyOwnIngredients: Boolean,
        onlyFavorites: Boolean,
        resetPage: Boolean = false
    ) {
        viewModelScope.launch {
            if (resetPage) currentPage = 0
            lastQuery = query
            lastSearchType = searchType
            lastDietTypes = dietTypes
            lastOwnIngredients = onlyOwnIngredients
            lastFavorites = onlyFavorites
            val result = when {
                onlyOwnIngredients -> recipeRepository.getRecipesByUserIngredients(10 * (currentPage + 1))
                else -> recipeRepository.searchRecipes(query, searchType, dietTypes, onlyFavorites, 10 * (currentPage + 1))
            }
            if (result.isSuccessful) {
                val dtoList = result.body() ?: emptyList()
                _recipes.value = dtoList.map { dto ->
                    Recipe(
                        id = dto.id,
                        title = dto.title,
                        description = null,
                        ingredients = emptyList(),
                        instructions = null,
                        imageUrl = dto.imageUrl,
                        dietTypes = dto.supportedDietTypes ?: emptyList()
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        currentPage++
        searchRecipes(lastQuery, lastSearchType, lastDietTypes, lastOwnIngredients, lastFavorites, resetPage = false)
    }

    fun onRecipeClicked(recipe: Recipe) {
        _navigateToDetail.value = recipe
    }

    fun onNavigatedToDetail() {
        _navigateToDetail.value = null
    }
} 