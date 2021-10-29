package com.sychev.facedetector.data.remote.model

import com.google.gson.annotations.SerializedName

data class CelebDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
)