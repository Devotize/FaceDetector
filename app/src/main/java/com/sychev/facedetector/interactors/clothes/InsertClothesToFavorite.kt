package com.sychev.facedetector.interactors.clothes

import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class InsertClothesToFavorite(
    private val detectedClothesRepository: DetectedClothesRepository
) {
    fun execute(detectedClothes: DetectedClothes): Flow<DataState<DetectedClothes>> = flow<DataState<DetectedClothes>> {
        detectedClothes.isFavorite = true
        emit(DataState.loading())
        detectedClothesRepository.updateDetectedClothes(detectedClothes)
        emit(DataState())
    }
}