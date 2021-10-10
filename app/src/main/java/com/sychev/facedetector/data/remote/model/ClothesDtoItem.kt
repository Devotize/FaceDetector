package com.sychev.facedetector.data.remote.model


import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("search_result")
    val searchResult: List<ClothesDtoItem>,
    @SerializedName("bubbles")
    val bubbles: List<String>,
)

data class ClothesDtoItem(
    @SerializedName("brand")
    val brand: String,
    @SerializedName("brand_logo")
    val brandLogo: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("colour")
    val colour: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("item_id")
    val itemId: String,
    @SerializedName("material")
    val material: String,
    @SerializedName("novice_flg")
    val noviceFlg: Int,
    @SerializedName("num_reviews")
    val numReviews: Double,
    @SerializedName("pic_url")
    val picUrl: String,
    @SerializedName("popular_flg")
    val popularFlg: String,
    @SerializedName("premium")
    val premium: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("price_discount")
    val priceDiscount: Double,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("size")
    val size: String,
    @SerializedName("subcategory")
    val subcategory: String,
    @SerializedName("url")
    val url: String
)