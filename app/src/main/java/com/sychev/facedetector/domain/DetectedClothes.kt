package com.sychev.facedetector.domain

import android.graphics.Bitmap

data class DetectedClothes(
    val url: String,
    val sourceImage: Bitmap,
    val gender: String,
    val itemCategory: String,
    var isFavorite: Boolean = false
) {
}