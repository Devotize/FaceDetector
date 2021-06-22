package com.sychev.facedetector.interactors.clothes

import android.util.Log
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetClothesList(
    private val detectedClothesRepository: DetectedClothesRepository
) {

    fun execute(): Flow<DataState<List<DetectedClothes>>> = flow <DataState<List<DetectedClothes>>>{
        try {
            emit(DataState.loading())

            val result = detectedClothesRepository.getDetectedClothesList()

            emit(DataState.success(result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error("error: ${e.message}"))
        }
    }

    fun execute(numOfElements: Int): Flow<DataState<List<DetectedClothes>>> = flow <DataState<List<DetectedClothes>>>{
        try {
            emit(DataState.loading())

            val result = detectedClothesRepository.getDetectedClothesList(numOfElements)

            emit(DataState.success(result))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error("error: ${e.message}"))
        }
    }
}