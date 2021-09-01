package com.sychev.facedetector.interactors.clothes_list

import android.content.Context
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ClothesFilters
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Exception

class SearchClothes(
    private val detectedClothesRepository: DetectedClothesRepository
) {
    fun execute(
        detectedClothes: DetectedClothes,
        context: Context,
    ): Flow<DataState<List<Clothes>>> = flow<DataState<List<Clothes>>> {
        try {
            emit(DataState.loading())

            val clothesList = detectedClothesRepository.searchClothes(detectedClothes = detectedClothes, context)

            detectedClothesRepository.insertClothesOrIgnoreIfFavorite(clothesList)

            val result = detectedClothesRepository.getClothesList(clothesList)

            if (result.isEmpty()) {
                throw Exception("Nothing found")
            }

//            val result = listOf<Clothes>(
//                Clothes(brand="MONOCEROS", gender="men", itemCategory="бордшорты", itemId="14060327", picUrl="https://images.wbstatic.net/c246x328/new/14060000/14060327-1.jpg", price=100, priceDiscount=90, provider="wildberries", rating=5, url="https://wildberries.ru/catalog/14060327/detail.aspx?targetUrl=ST", isFavorite=true),
//                Clothes(brand="Reebok Classic", gender="men", itemCategory="велосипедки", itemId="RTLAAI852401", picUrl="https://a.lmcdn.ru/img236x341/R/T/RTLAAI852401_14309112_1_v1_2x.jpg", price=100, priceDiscount=90, provider="lamoda", rating=5, url="https://lamoda.ru/p/rtlaai852401/clothes-reebokclassic-velosipedki/", isFavorite=true),
//            )

            emit(DataState.success(result))

        }catch (e: Exception) {
            emit(DataState.error("${e.message}"))
            e.printStackTrace()
        }
    }

    fun execute(query: String, size: Int): Flow<DataState<List<Clothes>>> = flow<DataState<List<Clothes>>>{
        try {
            emit(DataState.loading())
            val clothesList = detectedClothesRepository.searchClothesByQuery(query = query, size = size)
            detectedClothesRepository.insertClothesOrIgnoreIfFavorite(clothesList)

            val result = detectedClothesRepository.getClothesList(clothesList)
            emit(DataState.success(result))
        }catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error("${e.message}"))
        }
    }

    fun execute(
        filters: ClothesFilters
    ): Flow<DataState<List<Clothes>>> = flow<DataState<List<Clothes>>>{
        try {
            emit(DataState.loading())
            val clothesList = detectedClothesRepository.searchClothesByFilters(
                filters
            )
            detectedClothesRepository.insertClothesOrIgnoreIfFavorite(clothesList)

            val result = detectedClothesRepository.getClothesList(clothesList)
            emit(DataState.success(result))
        }catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error("${e.message}"))
        }
    }

}