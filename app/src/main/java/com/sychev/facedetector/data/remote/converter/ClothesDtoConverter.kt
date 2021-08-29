package com.sychev.facedetector.data.remote.converter

import com.sychev.facedetector.data.remote.model.SearchClothesResult.SearchResult.ClothesDto
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DomainMapper

class ClothesDtoConverter: DomainMapper<Clothes, ClothesDto> {

    override fun fromDomainModel(model: Clothes): ClothesDto {
        return ClothesDto(
            brand = model.brand,
            gender = model.gender,
            itemCategory = model.itemCategory,
            itemId = model.itemId,
            picUrl = model.picUrl,
            price = model.price,
            priceDiscount = model.priceDiscount,
            provider = model.provider,
            rating = model.rating,
            url = model.clothesUrl,
            color = model.color
        )
    }

    override fun toDomainModel(model: ClothesDto): Clothes {
        return Clothes(
            brand = model.brand,
            gender = model.gender,
            itemCategory = model.itemCategory,
            itemId = model.itemId,
            picUrl = model.picUrl,
            price = model.price,
            priceDiscount = model.priceDiscount,
            provider = model.provider,
            rating = model.rating,
            clothesUrl = model.url,
            color = model.color
        )
    }

    fun toDomainClothesList(dtoList: List<ClothesDto>): List<Clothes> {
        return dtoList.map {
            toDomainModel(it)
        }
    }


}