package com.sychev.facedetector.data.remote.converter

import android.graphics.Bitmap
import android.util.Base64
import com.sychev.facedetector.data.remote.model.BrandDto
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.utils.toBitmap

class BrandDtoConverter {

   fun toDomainModel(model: BrandDto): Brand {
       var btm: Bitmap? = null
       if (model.image.isNotEmpty()) {
           val bytes = Base64.decode(model.image, 0)
           btm = bytes.toBitmap()
       }
       return Brand(
           model.name,
           btm
       )
   }

    fun toDomainModelList(list: List<BrandDto>): List<Brand> {
        return list.map { toDomainModel(it) }
    }

}