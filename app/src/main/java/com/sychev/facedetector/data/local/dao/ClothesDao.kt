package com.sychev.facedetector.data.local.dao

import androidx.room.*
import com.sychev.facedetector.data.local.entity.DetectedClothesEntity

@Dao
interface ClothesDao {

    @Query("SELECT * FROM ${DetectedClothesEntity.TABLE_NAME}")
    suspend fun getAllClothes(): List<DetectedClothesEntity>

    @Query("SELECT * FROM ${DetectedClothesEntity.TABLE_NAME} WHERE url = :url")
    suspend fun getClothesByUrl(url: String): DetectedClothesEntity

    @Query("SELECT * FROM ${DetectedClothesEntity.TABLE_NAME} WHERE is_favorite = 1")
    suspend fun getAllFavoriteClothes():List<DetectedClothesEntity>

    @Update(entity = DetectedClothesEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDetectedClothes(detectedClothes: DetectedClothesEntity)

    @Insert(entity = DetectedClothesEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetectedClothes(detectedClothes: DetectedClothesEntity)

    @Delete(entity = DetectedClothesEntity::class)
    suspend fun deleteDetectedClothes(detectedClothes: DetectedClothesEntity)

    @Query("SELECT * FROM detected_clothes_table LIMIT (:itemCount)")
    suspend fun getClothes(itemCount: Int): List<DetectedClothesEntity>

    suspend fun insertOrIgnoreIfInFavorite(detectedClothesList: List<DetectedClothesEntity>) {
        detectedClothesList.forEach { detectedClothesEntity ->
            val clothes = getClothesByUrl(url = detectedClothesEntity.url)
            clothes?.let { detectedClothes ->
                detectedClothesEntity.isFavorite = detectedClothes.isFavorite
            }
            insertDetectedClothes(detectedClothesEntity)
        }
    }

    suspend fun getClothesListByUrl(clothesList: List<DetectedClothesEntity>): List<DetectedClothesEntity> {
        val list = ArrayList<DetectedClothesEntity>()
        clothesList.forEach {
            val detectedClothesEntity = getClothesByUrl(it.url)
            if (detectedClothesEntity != null) {
                list.add(detectedClothesEntity)
            }
        }
        return list
    }

}