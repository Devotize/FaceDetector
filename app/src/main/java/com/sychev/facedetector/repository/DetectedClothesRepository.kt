package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import retrofit2.http.Query

interface DetectedClothesRepository {

    suspend fun searchClothes(detectedClothes: DetectedClothes, context: Context): List<Clothes>

    suspend fun getClothesByUrl(url: String): Clothes

    suspend fun insertClothesToCache(clothesList: List<Clothes>)

    suspend fun updateClothes(clothes: Clothes)

    suspend fun getClothesList(clothesList: List<Clothes>): List<Clothes>

    suspend fun getClothesList(): List<Clothes>

    suspend fun getClothesList(numOfElements: Int): List<Clothes>

    suspend fun insertClothesOrIgnoreIfFavorite(clothesList: List<Clothes>)

    suspend fun getFavoriteClothes(): List<Clothes>

    suspend fun deleteClothesFromCache(clothes: Clothes)

    suspend fun getRandomPhotosUrl(
        accessKey: String,
        query: String,
        count: Int
    ): List<String>

    suspend fun getCelebPics(
        page: Int
    ): List<Bitmap>

}