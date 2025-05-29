package com.emreberkgoger.foodrecipeapp.data.model.user

enum class DietType(val displayName: String, val apiName: String) {
    VEGAN("Vegan", "vegan"),
    VEGETARIAN("Vejetaryen", "lacto ovo vegetarian"),
    KETO("Ketojenik", "ketogenic"),
    PALEO("Paleo", "paleo"),
    MEDITERRANEAN("Akdeniz", "mediterranean"),
    LOW_CARB("Düşük Karbonhidrat", "low carb"),
    HIGH_PROTEIN("Yüksek Protein", "high protein"),
    GLUTEN_FREE("Glutensiz", "gluten free"),
    DAIRY_FREE("Sütsüz", "dairy free"),
    WHOLE_30("Whole 30", "whole 30"),
    OTHER("Diğer", "other")
}