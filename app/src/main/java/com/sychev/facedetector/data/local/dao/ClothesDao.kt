package com.sychev.facedetector.data.local.dao

import androidx.room.*
import com.sychev.facedetector.data.local.entity.ClothesEntity

@Dao
interface ClothesDao {

    @Query("SELECT * FROM ${ClothesEntity.TABLE_NAME}")
    suspend fun getAllClothes(): List<ClothesEntity>

    @Query("SELECT * FROM ${ClothesEntity.TABLE_NAME} WHERE url = :url")
    suspend fun getClothesByUrl(url: String): ClothesEntity

    @Query("SELECT * FROM ${ClothesEntity.TABLE_NAME} WHERE is_favorite = 1")
    suspend fun getAllFavoriteClothes():List<ClothesEntity>

    @Update(entity = ClothesEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDetectedClothes(clothes: ClothesEntity)

    @Insert(entity = ClothesEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetectedClothes(clothes: ClothesEntity)

    @Delete(entity = ClothesEntity::class)
    suspend fun deleteDetectedClothes(clothes: ClothesEntity)

    @Query("SELECT * FROM detected_clothes_table LIMIT (:itemCount)")
    suspend fun getClothes(itemCount: Int): List<ClothesEntity>

    suspend fun insertOrIgnoreIfInFavorite(clothesList: List<ClothesEntity>) {
        clothesList.forEach { detectedClothesEntity ->
            val clothes = getClothesByUrl(url = detectedClothesEntity.url)
            clothes?.let { detectedClothes ->
                detectedClothesEntity.isFavorite = detectedClothes.isFavorite
            }
            insertDetectedClothes(detectedClothesEntity)
        }
    }

    suspend fun getClothesListByUrl(clothesList: List<ClothesEntity>): List<ClothesEntity> {
        val list = ArrayList<ClothesEntity>()
        clothesList.forEach {
            val detectedClothesEntity = getClothesByUrl(it.url)
            if (detectedClothesEntity != null) {
                list.add(detectedClothesEntity)
            }
        }
        return list
    }

}