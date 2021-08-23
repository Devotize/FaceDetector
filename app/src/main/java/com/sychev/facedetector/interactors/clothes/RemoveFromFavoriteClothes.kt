package com.sychev.facedetector.interactors.clothes

import android.util.Log
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.flow

class RemoveFromFavoriteClothes(
    private val repository: DetectedClothesRepository
) {
    fun execute(clothes: Clothes) = flow {
        try {
            emit(DataState.loading())
            clothes.isFavorite = false
            val result = repository.updateClothes(clothes)
            emit(DataState.success(result))
        }catch (e: Exception) {
            Log.d(TAG, "execute: error: ${e.message}")
            e.printStackTrace()
        }
    }

}