package com.sychev.facedetector.data.local.dao

import androidx.room.*
import com.sychev.facedetector.data.local.entity.ClothesDetectedEntity

@Dao
interface DetectedClothesDao {
    @Query("SELECT * FROM ${ClothesDetectedEntity.TABLE_NAME}")
    suspend fun getDetectedClothes(): List<ClothesDetectedEntity>

    @Insert(entity = ClothesDetectedEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetectedClothes(clotheDetecteds: List<ClothesDetectedEntity>): LongArray

    @Query("DELETE FROM ${ClothesDetectedEntity.TABLE_NAME}")
    suspend fun clearDetectedClothes()
}