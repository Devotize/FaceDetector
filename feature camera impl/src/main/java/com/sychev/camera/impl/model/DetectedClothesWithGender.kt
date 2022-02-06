package com.sychev.camera.impl.model

import com.sychev.feature.deafine.gender.api.CommonGender
import com.sychev.feature.define.clothes.api.DetectedClothes

data class DetectedClothesWithGender(
    val detectedClothes: DetectedClothes,
    val gender: CommonGender,
) {
}