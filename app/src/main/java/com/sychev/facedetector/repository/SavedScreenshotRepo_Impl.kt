package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.mapper.EntityMapper
import com.sychev.facedetector.data.remote.CelebDetectionApi
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.converter.DetectedClothesConverter
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.presentation.MainActivity
import com.sychev.facedetector.utils.TAG
import okhttp3.MediaType
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class SavedScreenshotRepo_Impl(
    private val screenshotDao: ScreenshotDao,
    private val entityMapper: EntityMapper,
    private val celebDetectionApi: CelebDetectionApi,
    private val clothesDetectionApi: ClothesDetectionApi,
    private val detectedClothesConverter: DetectedClothesConverter
)
    : SavedScreenshotRepo {
    override suspend fun getScreenshot(id: Int): SavedScreenshot {
        return entityMapper.toDomainModel(screenshotDao.getScreenshot(id))
    }

    override suspend fun addScreenshotToDb(screenshot: SavedScreenshot) {
        screenshotDao.insert(entityMapper.fromDomainModel(screenshot))
    }

    override suspend fun deleteScreenshot(screenshot: SavedScreenshot) {
        screenshotDao.delete(entityMapper.fromDomainModel(screenshot))
    }

    override suspend fun getAllScreenshots(): List<SavedScreenshot> {
        return entityMapper.toDomainList(screenshotDao.getAll())
    }

    override suspend fun findCelebrity(croppedFaces: List<Bitmap>): HashMap<Int, String> {
        val bodyHashMap: HashMap<Int, String> = HashMap()
        croppedFaces.forEachIndexed { index, bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArrayBitmap = stream.toByteArray()
//            Log.d(TAG, "findCelebrity: byteArrayBitmap.toString = ${byteArrayBitmap.toString(Charsets.UTF_8)}")
            bodyHashMap[index] = Base64.encodeToString(byteArrayBitmap, Base64.DEFAULT)
            
        }

        return celebDetectionApi.findCelebrity(bodyHashMap)
    }

    override suspend fun detectClothes(bitmap: Bitmap, context: Context): List<DetectedClothes> {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
        val byteArrayBitmap = stream.toByteArray()
        val bodyString = Base64.encodeToString(byteArrayBitmap, Base64.DEFAULT)

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

        return detectedClothesConverter.toDomainDetectedClothes(result)

    }

}