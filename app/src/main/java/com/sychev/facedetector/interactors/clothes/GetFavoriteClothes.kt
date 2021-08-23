package com.sychev.facedetector.interactors.clothes

import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFavoriteClothes(
    private val detectedClothesRepository: DetectedClothesRepository
) {

    fun execute(): Flow<DataState<List<Clothes>>> {
        return flow<DataState<List<Clothes>>>{
            try {
                emit(DataState.loading())

                val result = detectedClothesRepository.getFavoriteClothes()

                emit(DataState.success(result))

            }catch (e: Exception) {

            }
        }

    }

}