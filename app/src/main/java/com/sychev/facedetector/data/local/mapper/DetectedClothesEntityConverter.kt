package com.sychev.facedetector.data.local.mapper

import com.sychev.facedetector.data.local.entity.ClothesDetectedEntity
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toByteArray

class DetectedClothesEntityConverter: DomainMapper<DetectedClothes, ClothesDetectedEntity> {

    fun fromDomainModel(model: DetectedClothes): ClothesDetectedEntity {
        return ClothesDetectedEntity(
            title = model.title,
            confidence = model.confidence,
            detectedClass = model.detectedClass,
            sourceBitmap = model.sourceBitmap.toByteArray(),
            croppedBitmap = model.croppedBitmap.toByteArray(),
            gender = model.gender,
        )
    }

    override fun toDomainModel(model: ClothesDetectedEntity): DetectedClothes {
        return DetectedClothes(
            title = model.title,
            confidence = model.confidence,
            detectedClass = model.detectedClass,
            sourceBitmap = model.sourceBitmap.toBitmap(),
            croppedBitmap = model.croppedBitmap.toBitmap(),
            gender = model.gender,
        )
    }
}