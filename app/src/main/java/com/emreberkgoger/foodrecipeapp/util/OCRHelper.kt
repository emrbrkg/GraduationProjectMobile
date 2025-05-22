package com.emreberkgoger.foodrecipeapp.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object OCRHelper {
    // Bitmap'i geçici dosyaya çevir
    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String = "ocr_temp.jpg"): File {
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
        }
        return file
    }
}