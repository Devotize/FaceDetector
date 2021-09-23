package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItem
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.ml.ClothesTestModel
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ClothesFilters
import com.sychev.facedetector.presentation.ui.screen.shop_screen.TestClothesFilter

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

    suspend fun searchClothesByQuery(query: String, size: Int): List<Clothes>

    suspend fun searchClothesByFilters(
        filters: ClothesFilters,
    ): List<Clothes>

    suspend fun searchClothesByFilters(
        testClothesFilter: TestClothesFilter,
    ): List<Clothes>

    suspend fun getFilterValues(): List<FilterValuesDtoItem>

}