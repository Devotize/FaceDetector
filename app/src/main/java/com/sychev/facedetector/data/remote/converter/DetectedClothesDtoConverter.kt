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
                DetectedClothes(
                    it.searchResult[0].url,
                    it.sourceImg.decodeToBitmap(),
                    it.searchResult[0].gender,
                    it.searchResult[0].itemCategory,
                )
            )
        }
        return domainList
    }

    fun dummyConvert(): List<DetectedClothes> {
       return listOf(
            DetectedClothes(
                "sdf",
                Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888),
                "gender",
                "sdf"
            )
        )
    }

    override fun fromDomainModel(model: DetectedClothes): DetectedClothesDto.DetectedClothesDtoItem {
        return DetectedClothesDto.DetectedClothesDtoItem(
            listOf(),
            "",
            listOf(DetectedClothesDto.DetectedClothesDtoItem.SearchResult(
                "",
                "",
                "",
            )),
            "",
            ""
        )
    }

    override fun toDomainModel(model: DetectedClothesDto.DetectedClothesDtoItem): DetectedClothes {
        return DetectedClothes(
            model.searchResult[0].url,
            model.sourceImg.decodeToBitmap(),
            model.searchResult[0].gender,
            model.searchResult[0].itemCategory,
        )
    }

    fun toDomainDetectedClothesList(list: List<DetectedClothesDto.DetectedClothesDtoItem>): List<DetectedClothes> {
        Log.d(TAG, "toDomainDetectedClothesList: mappnig...")
        return list.map {
            toDomainModel(it)
        }
    }


}