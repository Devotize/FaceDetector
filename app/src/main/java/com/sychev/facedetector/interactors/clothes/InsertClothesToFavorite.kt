package com.sychev.facedetector.interactors.clothes

import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertClothesToFavorite(
    private val detectedClothesRepository: DetectedClothesRepository
) {
    fun execute(clothes: Clothes): Flow<DataState<Clothes>> = flow<DataState<Clothes>> {
        clothes.isFavorite = true
        emit(DataState.loading())
        detectedClothesRepository.updateClothes(clothes)
        emit(DataState())
    }
}