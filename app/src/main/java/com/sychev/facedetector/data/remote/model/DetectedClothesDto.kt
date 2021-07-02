package com.sychev.facedetector.data.remote.model


import com.google.gson.annotations.SerializedName

class DetectedClothesDto : ArrayList<DetectedClothesDto.DetectedClothesDtoItem>(){
    data class DetectedClothesDtoItem(
        @SerializedName("item_categories")
        val itemCategories: List<String>,
        @SerializedName("scenario")
        val scenario: String,
        @SerializedName("search_result")
        val searchResult: List<SearchResult>,
        @SerializedName("source_img")
        val sourceImg: String,
        @SerializedName("type")
        val type: String
    ) {
        data class SearchResult(
            @SerializedName("gender")
            val gender: String,
            @SerializedName("item_category")
            val itemCategory: String,
            @SerializedName("url")
            val url: String,
            @SerializedName("brand")
            val brand: String,
            @SerializedName("pic_url")
            val pictureUrl: String
        )
    }
}