package com.sychev.facedetector.domain

import android.graphics.Bitmap

data class DetectedClothes(
    val url: String,
    val gender: String,
    val itemCategory: String,
    val picUrl: String,
    val brand: String,
    var isFavorite: Boolean = false
) {
}