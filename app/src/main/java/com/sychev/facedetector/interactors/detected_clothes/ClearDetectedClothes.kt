package com.sychev.facedetector.interactors.detected_clothes

import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.flow

class ClearDetectedClothes(
    private val clothesRepository: DetectedClothesRepository
) {
    fun execute() = flow<DataState<Unit>> {
        try {
            emit(DataState.loading())
            val result = clothesRepository.clearDetectedClothesTable()
            emit(DataState.success(result))
        }catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error(" ${e.localizedMessage}"))
        }
    }
}