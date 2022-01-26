package com.sychev.facedetector.interactors.image

import android.content.Context
import android.graphics.Bitmap
import com.sychev.facedetector.domain.ImageData
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.repository.ImageDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject


class InsertImageToCache @Inject constructor(
    private val repository: ImageDataRepository
    ){
    fun execute(image: Bitmap, context: Context): Flow<DataState<ImageData>> = flow {
        try {
            emit(DataState.loading())
            val root = context.externalCacheDir.toString()
            val dir = File(root + ImageData.IMAGE_DATA_DIRECTORY)
            val uniqueName = UUID.randomUUID().toString() + ".jpg"
            val imageFile = File(dir, uniqueName)

            val out = FileOutputStream(imageFile)
            image.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()

            val imageData = ImageData(
                filename = uniqueName,
                image
            )
            emit(DataState.success(imageData))
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}