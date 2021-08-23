package com.sychev.facedetector.repository

import android.graphics.Bitmap
import android.util.Base64
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.mapper.SavedScreenshotConverter
import com.sychev.facedetector.data.remote.CelebDetectionApi
import com.sychev.facedetector.domain.SavedScreenshot
import java.io.ByteArrayOutputStream

class SavedScreenshotRepoImpl(
    private val screenshotDao: ScreenshotDao,
    private val savedScreenshotConverter: SavedScreenshotConverter,
    private val celebDetectionApi: CelebDetectionApi,
)
    : SavedScreenshotRepo {
    override suspend fun getScreenshot(id: Int): SavedScreenshot {
        return savedScreenshotConverter.toDomainModel(screenshotDao.getScreenshot(id))
    }

    override suspend fun addScreenshotToDb(screenshot: SavedScreenshot) {
        screenshotDao.insert(savedScreenshotConverter.fromDomainModel(screenshot))
    }

    override suspend fun deleteScreenshot(screenshot: SavedScreenshot) {
        screenshotDao.delete(savedScreenshotConverter.fromDomainModel(screenshot))
    }

    override suspend fun getAllScreenshots(): List<SavedScreenshot> {
        return savedScreenshotConverter.toDomainList(screenshotDao.getAll())
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

}