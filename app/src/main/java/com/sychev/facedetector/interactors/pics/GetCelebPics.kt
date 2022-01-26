package com.sychev.facedetector.interactors.pics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.celeb.Celeb
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCelebPics(
    private val repository: DetectedClothesRepository
) {

    fun execute(page: Int): Flow<DataState<List<Celeb>>> = flow<DataState<List<Celeb>>>{
        try {
            emit(DataState.loading())
//            Log.d(TAG, "execute: getCelebPics called")
            val result = repository.getCelebPics(page)
            emit(DataState.success(result))
//            Log.d(TAG, "execute: $result")
        }catch (e: Exception) {
            Log.d(TAG, "execute: exception: ${e.message}")
            e.printStackTrace()
            emit(DataState.error(" ${e.localizedMessage}"))
        }
    }


    fun fakeExecute(page: Int, context: Context): Flow<DataState<List<Celeb>>> = flow {
        emit(DataState.loading())
        delay(1000)
        val image = BitmapFactory.decodeResource(context.resources, R.drawable.default_own_img)
        val data = listOf<Celeb>(
            Celeb(
                name = "Nick Jonas",
                image = image
            ),
            Celeb(
                name = "Nick Jonas",
                image = image
            ),
            Celeb(
                name = "Nick Jonas",
                image = image
            ),
            Celeb(
                name = "Nick Jonas",
                image = image
            ),
            Celeb(
                name = "Nick Jonas",
                image = image
            ),
            Celeb(
                name = "Nick Jonas",
                image = image
            ),

        )
        emit(DataState.success(data))
    }
}