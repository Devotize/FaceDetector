package com.sychev.facedetector.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Clothes(
    val brand: String,
    val gender: String,
    val itemId: String,
    val picUrl: String,
    val price: Int,
    val priceDiscount: Double,
    val provider: String,
    val rating: Double,
    val clothesUrl: String,
    val itemCategory: String,
    val color: String,
    val brandLogo: String,
    val description: String,
    val material: String,
    val noviceFlg: Int,
    val numReviews: Double,
    val popularFlg: String,
    val premium: String,
    val size: String,
    val subcategory: String,
    var isFavorite: Boolean = false,
    var bubbles: List<String> = listOf()
): Parcelable {
}

data class ClothesWithBubbles(
    val clothes: List<Clothes>,
    val bubbles: List<String> = listOf()
)