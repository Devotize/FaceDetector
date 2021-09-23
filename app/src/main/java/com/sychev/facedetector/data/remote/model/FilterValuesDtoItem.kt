package com.sychev.facedetector.data.remote.model

data class FilterValuesDtoItem(
    val id: String,
    val name: String,
    val type: String,
    val values: List<String>?,
)