package com.sychev.feature.define.clothes.api

import android.graphics.RectF

data class DetectedClothes(
    val list: List<CommonDetectedClothes>
)

data class CommonDetectedClothes(
    val id: String = "-1",
    val title: String = "undefined",
    val confidence: Float = 1f,
    val location: RectF = RectF(),
    val detectedClass: Int = -1,
)