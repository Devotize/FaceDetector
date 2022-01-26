package com.sychev.facedetector.domain

import android.graphics.Bitmap

data class ImageData(
    val filename: String,
    val bitmap: Bitmap,
) {
    companion object {
        const val IMAGE_DATA_DIRECTORY: String = "/saved_images"
    }
}
