package com.sychev.facedetector.repository

import android.graphics.Bitmap
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.mapper.EntityMapper
import com.sychev.facedetector.data.remote.CelebDetectionApi
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.utils.TAG
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class SavedScreenshotRepo_Impl(
    private val screenshotDao: ScreenshotDao,
    private val entityMapper: EntityMapper,
    private val celebDetectionApi: CelebDetectionApi,
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

    private fun toBinary(bytes: ByteArray) {
        val sb = StringBuilder(bytes.size * Byte.SIZE_BYTES)
        Base64.encodeToString(bytes, Base64.DEFAULT)

    }

}