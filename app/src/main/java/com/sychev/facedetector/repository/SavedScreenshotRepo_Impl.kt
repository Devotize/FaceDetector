package com.sychev.facedetector.repository

import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.mapper.EntityMapper
import com.sychev.facedetector.domain.SavedScreenshot

class SavedScreenshotRepo_Impl(
    private val screenshotDao: ScreenshotDao,
    private val entityMapper: EntityMapper
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
}