package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import com.sychev.facedetector.domain.DetectedClothes

interface DetectedClothesRepository {

    suspend fun detectClothes(bitmap: Bitmap, context: Context): List<DetectedClothes>

    suspend fun insertDetectedClothesToCache(detectedClothesList: List<DetectedClothes>)

    suspend fun updateDetectedClothes(detectedClothes: DetectedClothes)

    suspend fun getDetectedClothesList(detectedClothesList: List<DetectedClothes>): List<DetectedClothes>

    suspend fun getDetectedClothesList(): List<DetectedClothes>

    suspend fun getDetectedClothesList(numOfElements: Int): List<DetectedClothes>

    suspend fun insertDetectedClothesOrIgnoreIfFavorite(detectedClothesList: List<DetectedClothes>)

    suspend fun getFavoriteDetectedClothes(): List<DetectedClothes>

    suspend fun deleteDetectedClothesFromCache(detectedClothes: DetectedClothes)



}