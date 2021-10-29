package com.sychev.facedetector.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ClothesDetectedEntity.TABLE_NAME)
class ClothesDetectedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "confidence")
    val confidence: Float = 1f,
    @ColumnInfo(name = "detected_class")
    val detectedClass: Int = -1,
    @ColumnInfo(name = "source_bitmap", typeAffinity = ColumnInfo.BLOB)
    val sourceBitmap: ByteArray,
    @ColumnInfo(name = "cropped_bitmap", typeAffinity = ColumnInfo.BLOB)
    val croppedBitmap: ByteArray,
    @ColumnInfo(name = "gender")
    var gender: String,
){
    companion object {
        const val TABLE_NAME = "clothes_detected_table"
    }
}