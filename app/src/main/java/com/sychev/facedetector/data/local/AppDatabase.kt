package com.sychev.facedetector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.entity.ScreenshotEntity

@Database(entities = [ScreenshotEntity::class], version = 7)
abstract class AppDatabase: RoomDatabase() {
    abstract fun screenshotDao(): ScreenshotDao
}