package com.sychev.facedetector.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Clothes(
    val brand: String,
    val gender: String,
    val itemId: String,
    val picUrl: String,
    val price: Int,
    val priceDiscount: Int,
    val provider: String,
    val rating: Int,
    val clothesUrl: String,
    val itemCategory: String,
    var isFavorite: Boolean = false
): Parcelable {
}