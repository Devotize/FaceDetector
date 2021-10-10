package com.sychev.facedetector.data.remote.model

import com.google.gson.annotations.SerializedName

data class BrandDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
) {
}