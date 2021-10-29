package com.sychev.facedetector.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ClothesEntity.TABLE_NAME)
data class ClothesEntity (
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
    val price: Double,
    @ColumnInfo(name = "rating")
    val rating: Double,
    @ColumnInfo(name = "price_Discount")
    val priceDiscount: Double,
    @ColumnInfo(name = "provider")
    val provider: String,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean,
    @ColumnInfo(name = "color")
    val color: String,
    @ColumnInfo(name = "brand_logo")
    val brandLogo: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "material")
    val material: String,
    @ColumnInfo(name = "novice_flg")
    val noviceFlg: Int,
    @ColumnInfo(name = "num_reviews")
    val numReviews: Double,
    @ColumnInfo(name = "popular_flg")
    val popularFlg: Int,
    @ColumnInfo(name = "premium")
    val premium: String,
    @ColumnInfo(name = "size")
    val size: String,
    @ColumnInfo(name = "subcategory")
    val subcategory: String,
    ){
    companion object{
        const val TABLE_NAME = "detected_clothes_table"
    }
}