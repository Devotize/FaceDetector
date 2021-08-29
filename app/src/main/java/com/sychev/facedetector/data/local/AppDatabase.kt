package com.sychev.facedetector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sychev.facedetector.data.local.dao.ClothesDao
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.entity.DetectedClothesEntity
import com.sychev.facedetector.data.local.entity.ScreenshotEntity

@Database(entities = [ScreenshotEntity::class, DetectedClothesEntity::class], version = 12)
abstract class AppDatabase: RoomDatabase() {
    abstract fun screenshotDao(): ScreenshotDao
    abstract fun detectedClothesDao(): ClothesDao
}