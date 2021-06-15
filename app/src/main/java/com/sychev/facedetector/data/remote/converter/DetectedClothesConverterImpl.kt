package com.sychev.facedetector.data.remote.converter

import com.sychev.facedetector.data.remote.model.DetectedClothesDto
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.utils.decodeToBitmap

class DetectedClothesConverterImpl: DetectedClothesConverter {

    override fun toDomainDetectedClothes(dtoModel: DetectedClothesDto): List<DetectedClothes> {
        val domainList = ArrayList<DetectedClothes>()
        dtoModel.forEach {
            domainList.add(
                DetectedClothes(
                    it.sourceImg.decodeToBitmap(),
                    it.searchResult[0].gender,
                    it.searchResult[0].itemCategory,
                    it.searchResult[0].url
                )
            )
        }
        return domainList
    }


}