package com.sychev.facedetector.interactors.pics

import android.graphics.Bitmap
import android.util.Log
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCelebPics(
    private val repository: DetectedClothesRepository
) {

    fun execute(page: Int): Flow<DataState<List<Bitmap>>> = flow<DataState<List<Bitmap>>>{
        try {
            emit(DataState.loading())
//            Log.d(TAG, "execute: getCelebPics called")
            val result = repository.getCelebPics(page)
            emit(DataState.success(result))
//            Log.d(TAG, "execute: $result")
        }catch (e: Exception) {
            Log.d(TAG, "execute: exception: ${e.message}")
            e.printStackTrace()
        }
    }

}