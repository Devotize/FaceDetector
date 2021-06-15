package com.sychev.facedetector.data.remote.converter

import com.sychev.facedetector.data.remote.model.DetectedClothesDto
import com.sychev.facedetector.domain.DetectedClothes

interface DetectedClothesConverter {

    fun toDomainDetectedClothes(dtoModel: DetectedClothesDto): List<DetectedClothes>

}