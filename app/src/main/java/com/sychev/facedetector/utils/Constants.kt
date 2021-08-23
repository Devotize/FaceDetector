package com.sychev.facedetector.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

const val TAG = "AppDebug"
const val MIN_SCORE = 0.6f

fun String.decodeToBitmap(): Bitmap {
    val imageBytes = Base64.decode(this, 0)
    return BitmapFactory.decodeByteArray(imageBytes, 0 , imageBytes.size)
}