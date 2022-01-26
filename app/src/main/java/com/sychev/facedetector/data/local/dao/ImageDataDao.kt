package com.sychev.facedetector.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sychev.facedetector.data.local.entity.ImageDataEntity

@Dao
interface ImageDataDao {
    @Query("SELECT * FROM ${ImageDataEntity.TABLE_NAME}")
    suspend fun getAllImageData(): List<ImageDataEntity>

    @Insert
    suspend fun insertImageData(imageDataEntity: ImageDataEntity): Long
}