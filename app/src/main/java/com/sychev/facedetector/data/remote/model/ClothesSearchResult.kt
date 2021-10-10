package com.sychev.facedetector.data.remote.model


import com.google.gson.annotations.SerializedName

data class SearchClothesResult(
    @SerializedName("item_categories")
    val itemCategories: List<String>?,
    @SerializedName("scenario")
    val scenario: String,
    @SerializedName("search_result")
    val globalSearchResult: GlobalSearchResult,
    @SerializedName("type")
    val type: String,
) {

    data class GlobalSearchResult(
        @SerializedName("lamoda")
        val lamoda: SearchResult,
        @SerializedName("wildberries")
        val wildberries: SearchResult,
    )
}