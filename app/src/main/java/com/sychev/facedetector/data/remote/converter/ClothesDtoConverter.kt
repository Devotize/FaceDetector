package com.sychev.facedetector.data.remote.converter

import com.sychev.facedetector.data.remote.model.ClothesDtoItem
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DomainMapper

class ClothesDtoConverter: DomainMapper<Clothes, ClothesDtoItem> {

//    override fun fromDomainModel(model: Clothes): ClothesDtoItem {
//        return ClothesDtoItem(
//            brand = model.brand,
//            gender = model.gender,
//            category = model.itemCategory,
//            itemId = model.itemId,
//            picUrl = model.picUrl,
//            price = model.price.toDouble(),
//            priceDiscount = model.priceDiscount,
//            provider = model.provider,
//            rating = model.rating.toString(),
//            url = model.clothesUrl,
//            colour = model.color,
//            brandLogo = model.brandLogo,
//            description = model.description,
//            material = model.material,
//            noviceFlg = model.noviceFlg.toString(),
//            numReviews = model.numReviews.toString(),
//            popularFlg = model.popularFlg.toString(),
//            premium = model.premium,
//            size = model.size,
//            subcategory = model.subcategory,
//        )
//    }

    override fun toDomainModel(model: ClothesDtoItem): Clothes {
        return Clothes(
            brand = model.brand,
            gender = model.gender,
            itemCategory = model.category,
            itemId = model.itemId,
            picUrl = model.picUrl,
            price = model.price.toInt(),
            priceDiscount = model.priceDiscount,
            provider = model.provider,
            rating = model.rating.toDouble(),
            clothesUrl = model.url,
            color = model.colour,
            brandLogo = model.brandLogo,
            description = model.description,
            material = model.material,
            noviceFlg = model.noviceFlg.toInt(),
            numReviews = model.numReviews.toDouble(),
            popularFlg = model.popularFlg.toInt(),
            premium = model.premium,
            size = model.size,
            subcategory = model.subcategory,
        )
    }

    fun toDomainClothesList(dtoList: List<ClothesDtoItem>): List<Clothes> {
        return dtoList.map {
            toDomainModel(it)
        }
    }


}