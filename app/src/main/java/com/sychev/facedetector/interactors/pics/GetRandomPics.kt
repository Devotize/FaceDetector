package com.sychev.facedetector.interactors.pics

import android.util.Log
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRandomPics(
    private val repository: DetectedClothesRepository
) {
    fun execute(
        accessKey: String,
        query: String,
        count: Int
    ): Flow<DataState<List<String>>> = flow<DataState<List<String>>> {
        try {
            emit(DataState.loading())
            val result = repository.getRandomPhotosUrl(accessKey, query, count)
            emit(DataState.success(result))
        }catch (e: Exception) {
            emit(DataState.error("error: ${e.message}"))
            Log.d(TAG, "execute: exception: ${e.message}")
            e.printStackTrace()
        }
    }
}