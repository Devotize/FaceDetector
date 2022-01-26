package com.sychev.facedetector.repository

import android.graphics.Bitmap

interface ImageDataRepository {
    suspend fun insertImageToCache(image: Bitmap): Long

    suspend fun getAllImages(): List<Bitmap>
}