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
import android.util.Log
import android.widget.Toast
import java.net.SocketTimeoutException

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

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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
        searchType: String,
        searchText: String,
        dietTypes: List<String>,
        onlyOwnIngredients: Boolean,
        onlyFavorites: Boolean,
        maxReadyTime: Int?,
        resetPage: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                if (resetPage) currentPage = 0
                lastSearchType = searchType
                lastQuery = searchText
                lastDietTypes = dietTypes
                lastOwnIngredients = onlyOwnIngredients
                lastFavorites = onlyFavorites
                val limit = 1 * (currentPage + 1)
                Log.d("RecipeSearchVM", "API çağrısı başlatılıyor: searchType=$searchType, searchText=$searchText, dietTypes=$dietTypes, onlyOwnIngredients=$onlyOwnIngredients, onlyFavorites=$onlyFavorites, maxReadyTime=$maxReadyTime, page=${currentPage + 1}")
                val result = when {
                    onlyOwnIngredients -> recipeRepository.getRecipesByUserIngredients(limit)
                    else -> {
                        val safeDietTypes = if (dietTypes.isNullOrEmpty()) null else dietTypes
                        val safeIngredients = if (searchType == "ingredient" && searchText.isNotBlank()) listOf(searchText) else null
                        val dto = if (searchType == "recipe") {
                            com.emreberkgoger.foodrecipeapp.data.dto.request.RecipeSearchDto(
                                query = searchText,
                                dietTypes = safeDietTypes,
                                ingredients = null,
                                useUserIngredients = null,
                                maxReadyTime = maxReadyTime,
                                limit = limit
                            )
                        } else {
                            com.emreberkgoger.foodrecipeapp.data.dto.request.RecipeSearchDto(
                                query = null,
                                dietTypes = safeDietTypes,
                                ingredients = safeIngredients,
                                useUserIngredients = null,
                                maxReadyTime = maxReadyTime,
                                limit = limit
                            )
                        }
                        recipeRepository.searchRecipes(dto)
                    }
                }
                Log.d("RecipeSearchVM", "API response: isSuccessful=${result.isSuccessful}, code=${result.code()}, message=${result.message()}")
                if (result.isSuccessful) {
                    val dtoList = result.body() ?: emptyList()
                    Log.d("RecipeSearchVM", "API success, gelen tarif sayısı: ${dtoList.size}")
                    // Favori tarifleri çek
                    val favoriteResponse = userRepository.getUserFavoriteRecipes()
                    val favoriteIds = if (favoriteResponse.isSuccessful) {
                        favoriteResponse.body()?.map { it.id } ?: emptyList()
                    } else emptyList()
                    _recipes.value = dtoList.map { dto ->
                        Recipe(
                            id = dto.id,
                            title = dto.title,
                            description = null,
                            ingredients = emptyList(),
                            instructions = null,
                            imageUrl = dto.imageUrl,
                            dietTypes = dto.supportedDietTypes ?: emptyList(),
                            isFavorite = dto.id in favoriteIds
                        )
                    }
                } else {
                    Log.e("RecipeSearchVM", "API error: code=${result.code()}, message=${result.message()}, errorBody=${result.errorBody()?.string()}")
                    _errorMessage.value = "Arama sırasında hata: ${result.message()}"
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Sunucuya ulaşılamıyor, lütfen tekrar deneyin."
            } catch (e: Exception) {
                _errorMessage.value = "Bir hata oluştu: ${e.localizedMessage ?: "Bilinmeyen hata"}"
            }
        }
    }

    fun loadNextPage() {
        currentPage++
        searchRecipes(lastSearchType, lastQuery, lastDietTypes, lastOwnIngredients, lastFavorites, null, resetPage = false)
    }

    fun onRecipeClicked(recipe: Recipe) {
        _navigateToDetail.value = recipe
    }

    fun onNavigatedToDetail() {
        _navigateToDetail.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun addFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val response = userRepository.addFavoriteRecipe(recipe.id)
            if (response.isSuccessful) {
                _recipes.value = _recipes.value.map {
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
                _recipes.value = _recipes.value.map {
                    if (it.id == recipe.id) it.copy(isFavorite = false) else it
                }
            } else {
                _errorMessage.value = "Favoriden çıkarılamadı: ${response.message()}"
            }
        }
    }
} 