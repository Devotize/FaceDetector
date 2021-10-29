package com.sychev.facedetector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sychev.facedetector.data.local.dao.ClothesDao
import com.sychev.facedetector.data.local.dao.DetectedClothesDao
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.entity.ClothesDetectedEntity
import com.sychev.facedetector.data.local.entity.ClothesEntity
import com.sychev.facedetector.data.local.entity.ScreenshotEntity

@Database(entities = [ScreenshotEntity::class, ClothesEntity::class, ClothesDetectedEntity::class], version = 21, exportSchema = false,)
abstract class AppDatabase: RoomDatabase() {
    abstract fun screenshotDao(): ScreenshotDao
    abstract fun clothesDao(): ClothesDao
    abstract fun detectedClothesDao(): DetectedClothesDao
}