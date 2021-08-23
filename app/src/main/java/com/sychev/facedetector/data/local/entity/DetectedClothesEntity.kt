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
    @ColumnInfo(name = "image")
    val image:String,
    @ColumnInfo(name = "gender")
    val gender: String,
    @ColumnInfo(name = "item_category")
    val itemCategory: String,
    @ColumnInfo(name = "brand")
    val brand: String,
    @ColumnInfo(name = "item_id")
    val itemId: String,
    @ColumnInfo(name = "price")
    val price: Int,
    @ColumnInfo(name = "rating")
    val rating: Int,
    @ColumnInfo(name = "price_Discount")
    val priceDiscount: Int,
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean,
    ){
    companion object{
        const val TABLE_NAME = "detected_clothes_table"
    }
}