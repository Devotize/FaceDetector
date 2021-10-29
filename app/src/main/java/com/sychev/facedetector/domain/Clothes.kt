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
    val popularFlg: Int,
    val premium: String,
    val size: String,
    val subcategory: String,
    var isFavorite: Boolean = false,
    var bubbles: List<String> = listOf()
): Parcelable {
    object NothingFoundClothes {
        //creating unique clothes with special itemId
        const val ITEM_ID_NOTHING_FOUND = "nothing_found_id"

        fun get() = Clothes(
            brand = "",
            gender = "",
            itemCategory = "",
            itemId = ITEM_ID_NOTHING_FOUND,
            picUrl = "",
            price = 0,
            priceDiscount = 0.0,
            provider = "",
            rating = 0.0,
            clothesUrl ="",
            color = "",
            brandLogo = "",
            description = "",
            material = "",
            noviceFlg = 0,
            numReviews = 0.0,
            popularFlg = 0,
            premium = "",
            size = "",
            subcategory = "",
        )
    }
}

data class ClothesWithBubbles(
    val clothes: List<Clothes>,
    val bubbles: List<String> = listOf()
)