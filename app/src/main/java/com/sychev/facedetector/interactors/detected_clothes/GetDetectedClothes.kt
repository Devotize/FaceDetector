package com.sychev.facedetector.interactors.detected_clothes

import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.flow

class GetDetectedClothes(
    private val clothesRepository: DetectedClothesRepository
) {
    fun execute() = flow<DataState<List<DetectedClothes>>> {
        try {
            emit(DataState.loading())
            val result = clothesRepository.getDetectedClothes()
            emit(DataState.success(result))
        }catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error(" ${e.localizedMessage}"))
        }
    }
}