package com.sychev.facedetector.data.local.mapper

import com.sychev.facedetector.data.local.entity.DetectedClothesEntity
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toByteArray

class DetectedClothesEntityConverter: DomainMapper<DetectedClothes, DetectedClothesEntity> {

    override fun fromDomainModel(model: DetectedClothes): DetectedClothesEntity {
        return DetectedClothesEntity(
            model.url,
            model.sourceImage.toByteArray(),
            model.gender,
            model.itemCategory,
            model.isFavorite
        )
    }

    override fun toDomainModel(model: DetectedClothesEntity): DetectedClothes {
        return DetectedClothes(
            model.url,
            model.image.toBitmap(),
            model.gender,
            model.itemCategory,
            model.isFavorite
        )
    }

    fun fromDomainList(list: List<DetectedClothes>): List<DetectedClothesEntity> {
        return list.map {
            fromDomainModel(it)
        }
    }

    fun toDomainModelList(list: List<DetectedClothesEntity>): List<DetectedClothes> {
        return list.map {
            toDomainModel(it)
        }
    }

}