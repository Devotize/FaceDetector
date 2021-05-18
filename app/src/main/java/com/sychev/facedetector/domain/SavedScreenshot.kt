package com.sychev.facedetector.domain

import android.graphics.Bitmap

data class SavedScreenshot(
    val id: Int,
    val image: Bitmap,
    val celebName: String
) {
}