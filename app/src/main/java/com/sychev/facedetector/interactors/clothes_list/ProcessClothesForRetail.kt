package com.sychev.facedetector.interactors.clothes_list

import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProcessClothesForRetail(
    private val repository: DetectedClothesRepository
) {
    fun execute(clothesList: List<Clothes>): Flow<DataState<List<Clothes>>> = flow<DataState<List<Clothes>>> {
        emit(DataState.loading())
        val result = clothesList.map {
            repository.getClothesByUrl(it.clothesUrl)
        }
        emit(DataState.success(result))
    }
}