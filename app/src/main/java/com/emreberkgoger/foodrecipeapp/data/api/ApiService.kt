package com.emreberkgoger.foodrecipeapp.data.api

import com.emreberkgoger.foodrecipeapp.data.dto.request.*
import com.emreberkgoger.foodrecipeapp.data.dto.response.*
import com.emreberkgoger.foodrecipeapp.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- USER ---
    @POST("users/register")
    suspend fun register(@Body registerRequest: UserRegisterDto): Response<UserDto>
    @POST("users/login")
    suspend fun login(@Body loginRequest: UserLoginDto): Response<JwtResponseDto>
    @GET("users/getCurrentUserProfile")
    suspend fun getCurrentUserProfile(): Response<ProfileDto>
    @PUT("users/update")
    suspend fun updateUserProfile(@Body updateRequest: UserUpdateDto): Response<ProfileDto>
    @PUT("users/updateDietPreferences")
    suspend fun updateDietPreferences(@Body dietTypes: List<String>): Response<ProfileDto>
    @GET("users/getUserIngredients")
    suspend fun getUserIngredients(): Response<List<UserIngredientResponseDto>>
    @POST("users/addUserIngredient")
    suspend fun addUserIngredient(@Body userIngredient: UserIngredientAddDto): Response<UserIngredientResponseDto>
    @POST("users/ingredients/batch")
    suspend fun addUserIngredients(@Body ingredients: List<UserIngredientAddDto>): Response<List<UserIngredientResponseDto>>
    @PUT("users/updateIngredients/{id}")
    suspend fun updateUserIngredient(@Path("id") userIngredientId: Long, @Body ingredient: UserIngredientAddDto): Response<UserIngredientResponseDto>
    @DELETE("users/removeIngredient/{id}")
    suspend fun removeUserIngredient(@Path("id") userIngredientId: Long): Response<Void>
    @GET("users/getFavoriteRecipes")
    suspend fun getUserFavoriteRecipes(): Response<List<RecipeDto>>
    @POST("users/addFavoriteRecipes/{recipeId}")
    suspend fun addFavoriteRecipe(@Path("recipeId") recipeId: Long): Response<Void>
    @DELETE("users/removeFavoriteRecipes/{recipeId}")
    suspend fun removeFavoriteRecipe(@Path("recipeId") recipeId: Long): Response<Void>
    @DELETE("users/deleteUser/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Long): Response<Void>

    // --- INGREDIENT ---
    @GET("ingredients")
    suspend fun getAllIngredients(): Response<List<IngredientDto>>
    @GET("ingredients/{id}")
    suspend fun getIngredientById(@Path("id") id: Long): Response<IngredientDto>
    @GET("ingredients/search")
    suspend fun searchIngredients(@Query("query") query: String): Response<List<IngredientDto>>
    @POST("ingredients")
    suspend fun saveIngredient(@Body ingredient: IngredientDto): Response<IngredientDto>
    @PUT("ingredients/{id}")
    suspend fun updateIngredient(@Path("id") id: Long, @Body ingredient: IngredientDto): Response<IngredientDto>
    @DELETE("ingredients/{id}")
    suspend fun deleteIngredient(@Path("id") id: Long): Response<Void>

    // --- RECIPE ---
    @POST("recipes/search")
    suspend fun searchRecipes(@Body searchDto: RecipeSearchDto): Response<List<RecipeDto>>
    @GET("recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: Long): Response<RecipeDetailDto>
    @GET("recipes/byIngredients")
    suspend fun getRecipesByIngredients(
        @Query("ingredients") ingredients: List<String>,
        @Query("limit") limit: Int = 10
    ): Response<List<RecipeDto>>
    @GET("recipes/byUserIngredients")
    suspend fun getRecipesByUserIngredients(@Query("limit") limit: Int = 10): Response<List<RecipeDto>>
    @GET("recipes/missingIngredients/{recipeId}")
    suspend fun getMissingIngredients(@Path("recipeId") recipeId: Long): Response<List<IngredientDto>>
    @POST("recipes/saveFromApi/{apiRecipeId}")
    suspend fun saveRecipeFromApi(@Path("apiRecipeId") apiRecipeId: Long): Response<RecipeDto>

    // --- NUTRITION ---
    @GET("nutrition/ingredient/{ingredientId}")
    suspend fun getNutritionForIngredient(@Path("ingredientId") ingredientId: Long): Response<NutritionDto>
    @GET("nutrition/recipe/{recipeId}")
    suspend fun getNutritionForRecipe(@Path("recipeId") recipeId: Long): Response<NutritionDto>
    @POST("nutrition/ingredient/{ingredientId}")
    suspend fun saveNutritionForIngredient(
        @Path("ingredientId") ingredientId: Long,
        @Body nutritionData: NutritionAddDto
    ): Response<NutritionDto>
    @DELETE("nutrition/{nutritionId}")
    suspend fun deleteNutrition(@Path("nutritionId") nutritionId: Long): Response<Void>

    // --- OCR ---
    @Multipart
    @POST("ocr/process-receipt")
    suspend fun processReceiptImage(@Part file: MultipartBody.Part): Response<OCRResultDto>
}