package com.sychev.facedetector.interactors.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.sychev.facedetector.domain.ImageData
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.ImageDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File


class GetImagesFromCache(
    private val repository: ImageDataRepository
) {
    fun execute(context: Context): Flow<DataState<List<ImageData>>> = flow {
        try {
            emit(DataState.loading())
            val folder =
                File(context.externalCacheDir.toString() + ImageData.IMAGE_DATA_DIRECTORY)
            folder.mkdirs()
            val allFiles = folder.listFiles { dir, name ->
                name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(
                    ".png"
                )
            }
            val imageDataList = allFiles.map {
                val btm = BitmapFactory.decodeFile(it.path)
                ImageData(
                    filename = it.name,
                    bitmap = btm,
                )
            }

            emit(DataState.success(imageDataList))
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}