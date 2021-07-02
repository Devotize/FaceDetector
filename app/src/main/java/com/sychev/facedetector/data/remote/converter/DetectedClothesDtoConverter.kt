package com.sychev.facedetector.data.remote.converter

import android.graphics.Bitmap
import android.util.Log
import com.sychev.facedetector.data.remote.model.DetectedClothesDto
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.decodeToBitmap

class DetectedClothesDtoConverter: DomainMapper<DetectedClothes, DetectedClothesDto.DetectedClothesDtoItem> {

    fun toDomainDetectedClothesList(dtoModel: DetectedClothesDto): List<DetectedClothes> {
        val domainList = ArrayList<DetectedClothes>()
        dtoModel.forEach {
            domainList.add(
                toDomainModel(it)
            )
        }
        return domainList
    }

    override fun fromDomainModel(model: DetectedClothes): DetectedClothesDto.DetectedClothesDtoItem {
        return DetectedClothesDto.DetectedClothesDtoItem(
            listOf(),
            "",
            listOf(DetectedClothesDto.DetectedClothesDtoItem.SearchResult(
                "",
                "",
                "",
                "",
                ""
            )),
            "",
            ""
        )
    }

    override fun toDomainModel(model: DetectedClothesDto.DetectedClothesDtoItem): DetectedClothes {
        return DetectedClothes(
            url = model.searchResult[0].url,
            picUrl = model.searchResult[0].pictureUrl,
            gender = model.searchResult[0].gender,
            itemCategory = model.searchResult[0].itemCategory,
            brand = model.searchResult[0].brand
        )
    }

    fun toDomainDetectedClothesList(list: List<DetectedClothesDto.DetectedClothesDtoItem>): List<DetectedClothes> {
        Log.d(TAG, "toDomainDetectedClothesList: mappnig...")
        return list.map {
            toDomainModel(it)
        }
    }


}