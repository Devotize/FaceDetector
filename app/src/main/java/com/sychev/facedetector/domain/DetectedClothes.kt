package com.sychev.facedetector.domain

import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetectedClothes(
    val id: String = "-1",
    val title: String = "undefined",
    val confidence: Float = 1f,
    val location: RectF = RectF(),
    val detectedClass: Int = -1,
    val sourceBitmap: Bitmap,
    val croppedBitmap: Bitmap,
    var gender: String,
): Parcelable