package com.sychev.facedetector.data.local.dao

//import androidx.room.*
//import com.sychev.facedetector.data.local.entity.ScreenshotEntity
//import com.sychev.facedetector.data.local.entity.ScreenshotEntity.Companion.TABLE_NAME
//
//@Dao
//interface ScreenshotDao {
//    @Query("SELECT * FROM $TABLE_NAME")
//    suspend fun getAll(): List<ScreenshotEntity>
//
//    @Query("SELECT * FROM $TABLE_NAME WHERE :id is id")
//    suspend fun getScreenshot(id: Int): ScreenshotEntity
//
//    @Insert(entity = ScreenshotEntity::class, onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(screenshotEntity: ScreenshotEntity)
//
//    @Delete(entity = ScreenshotEntity::class)
//    suspend fun delete(screenshotEntity: ScreenshotEntity)
//}