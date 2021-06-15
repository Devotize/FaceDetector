package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.SavedScreenshot

interface SavedScreenshotRepo {

    suspend fun getScreenshot(id: Int): SavedScreenshot

    suspend fun addScreenshotToDb(screenshot: SavedScreenshot)

    suspend fun deleteScreenshot(screenshot: SavedScreenshot)

    suspend fun getAllScreenshots(): List<SavedScreenshot>

    suspend fun findCelebrity(croppedFaces: List<Bitmap>): HashMap<Int, String>

    suspend fun detectClothes(bitmap: Bitmap, context: Context): List<DetectedClothes>

}