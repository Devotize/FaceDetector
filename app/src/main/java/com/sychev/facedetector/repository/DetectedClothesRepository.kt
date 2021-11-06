package com.sychev.facedetector.repository

import android.content.Context
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItem
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.ClothesWithBubbles
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.domain.celeb.Celeb
import com.sychev.facedetector.presentation.ui.screen.shop.ClothesFilter

interface DetectedClothesRepository {

    suspend fun searchClothes(detectedClothes: DetectedClothes, context: Context, size: Int): List<Clothes>

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
    ): List<Celeb>

    suspend fun searchClothesByQuery(query: String, size: Int): List<Clothes>

    suspend fun searchClothesByFilters(
        clothesFilter: ClothesFilter,
    ): ClothesWithBubbles

    suspend fun getFilterValues(): List<FilterValuesDtoItem>

    suspend fun getTopBrands(): List<Brand>

    suspend fun getDetectedClothes(): List<DetectedClothes>

    suspend fun insertDetectedClothes(detectedClothes: List<DetectedClothes>): LongArray

    suspend fun clearDetectedClothesTable()

}