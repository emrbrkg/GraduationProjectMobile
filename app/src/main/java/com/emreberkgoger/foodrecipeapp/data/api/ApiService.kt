package com.emreberkgoger.foodrecipeapp.data.api

import com.emreberkgoger.foodrecipeapp.data.dto.request.*
import com.emreberkgoger.foodrecipeapp.data.dto.response.*
import com.emreberkgoger.foodrecipeapp.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- USER ---
    @POST("api/users/register")
    suspend fun register(@Body registerRequest: UserRegisterDto): Response<UserDto>
    @POST("api/users/login")
    suspend fun login(@Body loginRequest: UserLoginDto): Response<JwtResponseDto>
    @GET("api/users/getCurrentUserProfile")
    suspend fun getCurrentUserProfile(): Response<ProfileDto>
    @PUT("api/users/update")
    suspend fun updateUserProfile(@Body updateRequest: UserUpdateDto): Response<ProfileDto>
    @PUT("api/users/updateDietPreferences")
    suspend fun updateDietPreferences(@Body dietTypes: List<String>): Response<ProfileDto>
    @GET("api/users/getUserIngredients")
    suspend fun getUserIngredients(): Response<List<UserIngredientResponseDto>>
    @POST("api/users/addUserIngredient")
    suspend fun addUserIngredient(@Body userIngredient: UserIngredientAddDto): Response<UserIngredientResponseDto>
    @POST("api/users/ingredients/batch")
    suspend fun addUserIngredients(@Body ingredients: List<UserIngredientAddDto>): Response<List<UserIngredientResponseDto>>
    @PUT("api/users/updateIngredients/{id}")
    suspend fun updateUserIngredient(@Path("id") userIngredientId: Long, @Body ingredient: UserIngredientAddDto): Response<UserIngredientResponseDto>
    @DELETE("api/users/removeIngredient/{id}")
    suspend fun removeUserIngredient(@Path("id") userIngredientId: Long): Response<Void>
    @GET("api/users/getFavoriteRecipes")
    suspend fun getUserFavoriteRecipes(): Response<List<RecipeDto>>
    @POST("api/users/addFavoriteRecipes/{recipeId}")
    suspend fun addFavoriteRecipe(@Path("recipeId") recipeId: Long): Response<Void>
    @DELETE("api/users/removeFavoriteRecipes/{recipeId}")
    suspend fun removeFavoriteRecipe(@Path("recipeId") recipeId: Long): Response<Void>
    @DELETE("api/users/deleteUser/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long): Response<Void>

    // --- INGREDIENT ---
    @GET("api/ingredients")
    suspend fun getAllIngredients(): Response<List<IngredientDto>>
    @GET("api/ingredients/{id}")
    suspend fun getIngredientById(@Path("id") id: Long): Response<IngredientDto>
    @GET("api/ingredients/search")
    suspend fun searchIngredients(@Query("query") query: String): Response<List<IngredientDto>>
    @POST("api/ingredients")
    suspend fun saveIngredient(@Body ingredient: IngredientDto): Response<IngredientDto>
    @PUT("api/ingredients/{id}")
    suspend fun updateIngredient(@Path("id") id: Long, @Body ingredient: IngredientDto): Response<IngredientDto>
    @DELETE("api/ingredients/{id}")
    suspend fun deleteIngredient(@Path("id") id: Long): Response<Void>

    // --- RECIPE ---
    @POST("api/recipes/search")
    suspend fun searchRecipes(@Body searchDto: RecipeSearchDto): Response<List<RecipeDto>>
    @GET("api/recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: Long): Response<RecipeDetailDto>
    @GET("api/recipes/byIngredients")
    suspend fun getRecipesByIngredients(
        @Query("ingredients") ingredients: List<String>,
        @Query("limit") limit: Int = 1
    ): Response<List<RecipeDto>>
    @GET("api/recipes/byUserIngredients")
    suspend fun getRecipesByUserIngredients(@Query("limit") limit: Int = 1): Response<List<RecipeDto>>
    @GET("api/recipes/missingIngredients/{recipeId}")
    suspend fun getMissingIngredients(@Path("recipeId") recipeId: Long): Response<List<IngredientDto>>
    @POST("api/recipes/saveFromApi/{apiRecipeId}")
    suspend fun saveRecipeFromApi(@Path("apiRecipeId") apiRecipeId: Long): Response<RecipeDto>

    // --- NUTRITION ---
    @GET("api/nutrition/ingredient/{ingredientId}")
    suspend fun getNutritionForIngredient(@Path("ingredientId") ingredientId: Long): Response<NutritionDto>
    @GET("api/nutrition/recipe/{recipeId}")
    suspend fun getNutritionForRecipe(@Path("recipeId") recipeId: Long): Response<NutritionDto>
    @POST("api/nutrition/ingredient/{ingredientId}")
    suspend fun saveNutritionForIngredient(
        @Path("ingredientId") ingredientId: Long,
        @Body nutritionData: NutritionAddDto
    ): Response<NutritionDto>
    @DELETE("api/nutrition/{nutritionId}")
    suspend fun deleteNutrition(@Path("nutritionId") nutritionId: Long): Response<Void>

    // --- OCR ---
    @Multipart
    @POST("api/ocr/process-receipt")
    suspend fun processReceiptImage(@Part file: MultipartBody.Part): Response<OCRResultDto>
}