package com.sychev.facedetector.interactors.clothes

import android.util.Log
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteClothes(
    private val detectedClothesRepository: DetectedClothesRepository,
) {
    fun execute(clothes: Clothes): Flow<DataState<Clothes>> = flow<DataState<Clothes>> {
        try {
            emit(DataState.loading())
            Log.d(TAG, "execute: called")
            detectedClothesRepository.deleteClothesFromCache(clothes)

            emit(DataState())
        }catch (e: Exception) {
            e.message?.let {
                emit(DataState.error(it))
            }
            e.printStackTrace()
        }


    }

}