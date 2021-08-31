package com.sychev.facedetector.interactors.clothes

import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetClothesList(
    private val detectedClothesRepository: DetectedClothesRepository
) {

    fun execute(favoriteOnly: Boolean): Flow<DataState<List<Clothes>>> = flow <DataState<List<Clothes>>>{
        try {
            emit(DataState.loading())
            val result: List<Clothes> = if (favoriteOnly) {
                detectedClothesRepository.getFavoriteClothes()
            } else {
                detectedClothesRepository.getClothesList()
            }

            emit(DataState.success(result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error("error: ${e.message}"))
        }
    }

    fun execute(numOfElements: Int): Flow<DataState<List<Clothes>>> = flow <DataState<List<Clothes>>>{
        try {
            emit(DataState.loading())

            val result = detectedClothesRepository.getClothesList(numOfElements)

            emit(DataState.success(result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error("error: ${e.message}"))
        }
    }

    fun execute(clothesList: List<Clothes>): Flow<DataState<List<Clothes>>> = flow <DataState<List<Clothes>>>{
        try {
            emit(DataState.loading())

            val result = detectedClothesRepository.getClothesList(clothesList)

            emit(DataState.success(result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error("error: ${e.message}"))
        }
    }
}