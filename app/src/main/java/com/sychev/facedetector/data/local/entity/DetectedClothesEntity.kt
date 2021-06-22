package com.sychev.facedetector.data.local.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = DetectedClothesEntity.TABLE_NAME)
data class DetectedClothesEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray,
    @ColumnInfo(name = "gender")
    val gender: String,
    @ColumnInfo(name = "item_category")
    val itemCategory: String,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean
    ){
    companion object{
        const val TABLE_NAME = "detected_clothes_table"
    }
}