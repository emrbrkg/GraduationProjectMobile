package com.emreberkgoger.foodrecipeapp.data.model.user

enum class DietType(val displayName: String) {
    VEGAN("Vegan"),
    VEGETARIAN("Vegetaryen"),
    KETO("Ketojenik"),
    PALEO("Paleo"),
    MEDITERRANEAN("Akdeniz"),
    LOW_CARB("Düşük Karbonhidrat"),
    HIGH_PROTEIN("Yüksek Protein"),
    GLUTEN_FREE("Glutensiz"),
    OTHER("Diğer")
}