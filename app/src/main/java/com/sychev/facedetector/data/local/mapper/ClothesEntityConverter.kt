package com.sychev.facedetector.data.local.mapper

import com.sychev.facedetector.data.local.entity.DetectedClothesEntity
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DomainMapper

class ClothesEntityConverter: DomainMapper<Clothes, DetectedClothesEntity> {

    override fun fromDomainModel(model: Clothes): DetectedClothesEntity {
        return DetectedClothesEntity(
            url = model.clothesUrl,
            image = model.picUrl,
            gender = model.gender,
            itemCategory = model.itemCategory,
            isFavorite = model.isFavorite,
            brand = model.brand,
            itemId = model.itemId,
            price = model.price,
            rating = model.rating,
            priceDiscount = model.priceDiscount,
            provider = model.provider,
        )
    }

    override fun toDomainModel(model: DetectedClothesEntity): Clothes {
        return Clothes(
            clothesUrl = model.url,
            picUrl = model.image,
            gender = model.gender,
            itemCategory = model.itemCategory,
            isFavorite = model.isFavorite,
            brand = model.brand,
            itemId = model.itemId,
            price = model.price,
            rating = model.rating,
            priceDiscount = model.priceDiscount,
            provider = model.provider,
        )
    }

    fun fromDomainList(list: List<Clothes>): List<DetectedClothesEntity> {
        return list.map {
            fromDomainModel(it)
        }
    }

    fun toDomainModelList(list: List<DetectedClothesEntity>): List<Clothes> {
        return list.map {
            toDomainModel(it)
        }
    }

}