package com.sychev.feature.define.clothes.api

import android.graphics.Bitmap

interface ClothesDefiner {
    fun defineClothes(bitmap: Bitmap): DetectedClothes
}