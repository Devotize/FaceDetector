package com.sychev.facedetector.data.remote.model


import com.google.gson.annotations.SerializedName

data class SearchClothesResult(
    @SerializedName("item_categories")
    val itemCategories: List<String>?,
    @SerializedName("scenario")
    val scenario: String,
    @SerializedName("search_result")
    val searchResult: SearchResult,
    @SerializedName("type")
    val type: String
) {
    data class SearchResult(
        @SerializedName("lamoda")
        val lamoda: List<ClothesDto>,
        @SerializedName("wildberries")
        val wildberries: List<ClothesDto>
    ) {
        data class ClothesDto(
            @SerializedName("brand")
            val brand: String,
            @SerializedName("gender")
            val gender: String,
            @SerializedName("category")
            val itemCategory: String,
            @SerializedName("item_id")
            val itemId: String,
            @SerializedName("pic_url")
            val picUrl: String,
            @SerializedName("price")
            val price: Int,
            @SerializedName("price_discount")
            val priceDiscount: Int,
            @SerializedName("provider")
            val provider: String,
            @SerializedName("rating")
            val rating: Int,
            @SerializedName("url")
            val url: String,
            @SerializedName("colour")
            val color: String,
        )

    }
}