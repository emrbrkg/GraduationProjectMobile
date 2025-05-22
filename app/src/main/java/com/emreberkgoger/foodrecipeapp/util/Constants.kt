package com.emreberkgoger.foodrecipeapp.util

object Constants {
    // API
    const val BASE_URL = "https://api.spoonacular.com"

    // Spoonacular (eğer backend'de kullanıyorsan, burada tutabilirsin)
    const val SPOONACULAR_API_KEY = "fbb6e5cf954b476686e1d2e5c8c0c051"

    // OCR için minimum resim boyutu, kalite vs.
    const val MIN_IMAGE_WIDTH = 300
    const val MIN_IMAGE_HEIGHT = 300

    // SharedPreferences anahtarları
    const val PREFS_NAME = "food_recipe_prefs"
    const val TOKEN_KEY = "jwt_token"

    // Diğer sabitler
    const val DEFAULT_TIMEOUT = 30L // saniye
}