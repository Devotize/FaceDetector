package com.sychev.facedetector.interactors.detected_clothes

import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.flow

class InsertDetectedClothes(
    private val clothesRepository: DetectedClothesRepository
){
    fun execute(detectedClothes: List<DetectedClothes>) = flow<DataState<LongArray>> {
        try {
            emit(DataState.loading())
            val result = clothesRepository.insertDetectedClothes(detectedClothes = detectedClothes)
            emit(DataState.success(result))
        }catch (e: Exception) {
            e.printStackTrace()
            emit(DataState.error(" ${e.localizedMessage}"))
        }
    }
}