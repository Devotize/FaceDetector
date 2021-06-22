package com.sychev.facedetector.interactors.clothes

import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteClothes(
    private val detectedClothesRepository: DetectedClothesRepository,
) {
    fun execute(detectedClothes: DetectedClothes): Flow<DataState<DetectedClothes>> = flow<DataState<DetectedClothes>> {
        try {
            emit(DataState.loading())

            detectedClothesRepository.deleteDetectedClothesFromCache(detectedClothes)

            emit(DataState())
        }catch (e: Exception) {
            e.message?.let {
                emit(DataState.error(it))
            }
            e.printStackTrace()
        }


    }

}