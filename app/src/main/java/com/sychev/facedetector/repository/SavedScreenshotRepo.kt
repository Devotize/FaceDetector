package com.sychev.facedetector.repository

import com.sychev.facedetector.domain.SavedScreenshot

interface SavedScreenshotRepo {

    suspend fun getScreenshot(id: Int): SavedScreenshot

    suspend fun addScreenshotToDb(screenshot: SavedScreenshot)

    suspend fun deleteScreenshot(screenshot: SavedScreenshot)

    suspend fun getAllScreenshots(): List<SavedScreenshot>

}