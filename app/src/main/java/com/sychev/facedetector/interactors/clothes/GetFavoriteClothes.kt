package com.sychev.facedetector.interactors.clothes

import android.util.Log
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFavoriteClothes(
    private val detectedClothesRepository: DetectedClothesRepository
) {

    fun execute(): Flow<DataState<List<DetectedClothes>>> {
        return flow<DataState<List<DetectedClothes>>>{
            try {
                emit(DataState.loading())

                val result = detectedClothesRepository.getFavoriteDetectedClothes()

                emit(DataState.success(result))

            }catch (e: Exception) {

            }
        }

    }

}