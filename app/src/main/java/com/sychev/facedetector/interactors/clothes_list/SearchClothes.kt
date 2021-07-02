package com.sychev.facedetector.interactors.clothes_list

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.sychev.facedetector.data.local.dao.DetectedClothesDao
import com.sychev.facedetector.data.local.mapper.DetectedClothesEntityConverter
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.converter.DetectedClothesDtoConverter
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import java.io.IOException
import java.lang.Exception

class SearchClothes(
    private val detectedClothesRepository: DetectedClothesRepository
) {
    fun execute(
        bitmap: Bitmap,
        context: Context
    ): Flow<DataState<List<DetectedClothes>>> = flow {
        try {
            emit(DataState.loading())

            val clothesList = detectedClothesRepository.detectClothes(bitmap, context)

            detectedClothesRepository.insertDetectedClothesOrIgnoreIfFavorite(clothesList)

            val result = detectedClothesRepository.getDetectedClothesList(clothesList)

            emit(DataState.success(result))

        }catch (e: Exception) {
            emit(DataState.error("${e.message}"))
            e.printStackTrace()
        }

    }
}