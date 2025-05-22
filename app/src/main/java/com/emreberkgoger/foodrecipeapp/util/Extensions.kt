package com.emreberkgoger.foodrecipeapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Tarih stringini okunabilir formata çevir
@RequiresApi(Build.VERSION_CODES.O)
fun String.toReadableDate(): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(this, formatter)
        dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    } catch (e: Exception) {
        this
    }
}

// İlk harfi büyük yap
fun String.capitalizeFirst(): String = replaceFirstChar { it.uppercase() }

// Float? -> String (örn: 12.0 -> "12", 12.5 -> "12.5")
fun Float?.toCleanString(): String = this?.let { if (it % 1 == 0f) it.toInt().toString() else it.toString() } ?: "0"