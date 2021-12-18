package com.sychev.facedetector.interactors.brand

import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTopBrands(
    private val repository: DetectedClothesRepository
) {
    fun execute(): Flow<DataState<List<Brand>>> = flow<DataState<List<Brand>>> {
        try {
            emit(DataState.loading())
            val result = repository.getTopBrands()
            emit(DataState.success(result))
        }catch (e: Exception) {
            emit(DataState.error(" ${e.localizedMessage}"))
            e.printStackTrace()
        }

    }
}