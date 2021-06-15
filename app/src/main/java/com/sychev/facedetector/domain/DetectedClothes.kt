package com.sychev.facedetector.domain

import android.graphics.Bitmap

data class DetectedClothes(
    val sourceImage: Bitmap,
    val gender: String,
    val itemCategory: String,
    val url: String
) {
}