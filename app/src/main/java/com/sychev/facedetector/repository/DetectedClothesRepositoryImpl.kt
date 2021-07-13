package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.sychev.facedetector.data.local.dao.DetectedClothesDao
import com.sychev.facedetector.data.local.mapper.DetectedClothesEntityConverter
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.converter.DetectedClothesDtoConverter
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.utils.TAG
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class DetectedClothesRepositoryImpl(
    private val clothesDetectionApi: ClothesDetectionApi,
    private val detectedClothesDao: DetectedClothesDao,
    private val detectedClothesEntityConverter: DetectedClothesEntityConverter,
    private val detectedClothesDtoConverter: DetectedClothesDtoConverter
): DetectedClothesRepository {

    override suspend fun detectClothes(bitmap: Bitmap, context: Context): List<DetectedClothes> {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
        val byteArrayBitmap = stream.toByteArray()
//        val bodyString = Base64.encodeToString(byteArrayBitmap, Base64.DEFAULT)

        val file = File(context.cacheDir,"image_test.jpg")
        file.createNewFile()
        val fos = FileOutputStream(file)
        fos.write(byteArrayBitmap)
        fos.flush()
        fos.close()

        val requestBody: RequestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("img", file.name ,file.asRequestBody("image/jpg".toMediaTypeOrNull()))
                .build()


        val result = clothesDetectionApi.detectClothes(requestBody)

        return detectedClothesDtoConverter.toDomainDetectedClothesList(result)
    }

    override suspend fun insertDetectedClothesToCache(detectedClothesList: List<DetectedClothes>) {
        Log.d(TAG, "insertDetectedClothesToCache: called detectedClothesList = $detectedClothesList")
        detectedClothesList.forEach {
            detectedClothesDao.insertDetectedClothes(detectedClothesEntityConverter.fromDomainModel(it))
        }
    }

    override suspend fun updateDetectedClothes(detectedClothes: DetectedClothes) {
        detectedClothesDao.updateDetectedClothes(detectedClothesEntityConverter.fromDomainModel(detectedClothes))
    }

    override suspend fun getDetectedClothesList(detectedClothesList: List<DetectedClothes>): List<DetectedClothes> {
        return detectedClothesEntityConverter.toDomainModelList(detectedClothesDao.getClothesListByUrl(detectedClothesEntityConverter.fromDomainList(detectedClothesList)))

    }

    override suspend fun getDetectedClothesList(): List<DetectedClothes> {
        return detectedClothesEntityConverter.toDomainModelList(detectedClothesDao.getAllClothes())
    }

    override suspend fun getDetectedClothesList(numOfElements: Int): List<DetectedClothes> {
        return detectedClothesEntityConverter.toDomainModelList(detectedClothesDao.getClothes(numOfElements))
    }

    override suspend fun insertDetectedClothesOrIgnoreIfFavorite(detectedClothesList: List<DetectedClothes>) {
        detectedClothesDao.insertOrIgnoreIfInFavorite(detectedClothesEntityConverter.fromDomainList(detectedClothesList))
    }

    override suspend fun getFavoriteDetectedClothes(): List<DetectedClothes> {
        return detectedClothesEntityConverter.toDomainModelList(detectedClothesDao.getAllFavoriteClothes())
    }

    override suspend fun deleteDetectedClothesFromCache(detectedClothes: DetectedClothes) {
        Log.d(TAG, "deleteDetectedClothesFromCache: called")
        detectedClothesDao.deleteDetectedClothes(detectedClothesEntityConverter.fromDomainModel(detectedClothes))
    }
}