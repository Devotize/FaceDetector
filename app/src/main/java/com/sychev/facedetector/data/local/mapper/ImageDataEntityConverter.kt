package com.sychev.facedetector.data.local.mapper

import android.graphics.Bitmap
import com.sychev.facedetector.data.local.entity.ImageDataEntity
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toByteArray

class ImageDataEntityConverter: DomainMapper<Bitmap, ImageDataEntity> {
    fun fromDomainModel(model: Bitmap): ImageDataEntity {
        return ImageDataEntity(
            data = model.toByteArray(),
        )
    }

    override fun toDomainModel(model: ImageDataEntity): Bitmap {
        return model.data.toBitmap()
    }
}