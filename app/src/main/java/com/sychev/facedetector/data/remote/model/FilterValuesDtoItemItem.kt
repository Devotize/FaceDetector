package com.sychev.facedetector.data.remote.model

import com.google.gson.annotations.SerializedName

data class FilterValuesDtoItem(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("range")
    val range: List<Int>?,
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String,
    @SerializedName("values")
    val values: List<Any>?
)