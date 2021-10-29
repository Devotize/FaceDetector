package com.sychev.facedetector.data.local.mapper

import com.sychev.facedetector.data.local.entity.ClothesEntity
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DomainMapper

class ClothesEntityConverter: DomainMapper<Clothes, ClothesEntity> {

    override fun fromDomainModel(model: Clothes): ClothesEntity {
        return ClothesEntity(
            url = model.clothesUrl,
            image = model.picUrl,
            gender = model.gender,
            itemCategory = model.itemCategory,
            isFavorite = model.isFavorite,
            brand = model.brand,
            itemId = model.itemId,
            price = model.price.toDouble(),
            rating = model.rating,
            priceDiscount = model.priceDiscount,
            provider = model.provider,
            color = model.color,
            brandLogo = model.brandLogo,
            description = model.description,
            material = model.material,
            noviceFlg = model.noviceFlg,
            numReviews = model.numReviews,
            popularFlg = model.popularFlg,
            premium = model.premium,
            size = model.size,
            subcategory = model.subcategory,
        )
    }

    override fun toDomainModel(model: ClothesEntity): Clothes {
        return Clothes(
            clothesUrl = model.url,
            picUrl = model.image,
            gender = model.gender,
            itemCategory = model.itemCategory,
            isFavorite = model.isFavorite,
            brand = model.brand,
            itemId = model.itemId,
            price = model.price.toInt(),
            rating = model.rating,
            priceDiscount = model.priceDiscount,
            provider = model.provider,
            color = model.color,
            brandLogo = model.brandLogo,
            description = model.description,
            material = model.material,
            noviceFlg = model.noviceFlg,
            numReviews = model.numReviews,
            popularFlg = model.popularFlg,
            premium = model.premium,
            size = model.size,
            subcategory = model.subcategory,
        )
    }

    fun fromDomainList(list: List<Clothes>): List<ClothesEntity> {
        return list.map {
            fromDomainModel(it)
        }
    }

    fun toDomainModelList(list: List<ClothesEntity>): List<Clothes> {
        return list.map {
            toDomainModel(it)
        }
    }

}