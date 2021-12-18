package com.sychev.facedetector.data.remote.converter

import android.util.Base64
import com.sychev.facedetector.data.remote.model.CelebDto
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.domain.celeb.Celeb
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toByteArray

class CelebDtoConverter : DomainMapper<Celeb, CelebDto> {
    fun fromDomainModel(model: Celeb): CelebDto {
        val bytes = model.image.toByteArray()
        return CelebDto(
            model.name,
            bytes.toString()
        )
    }

    override fun toDomainModel(model: CelebDto): Celeb {
        val bytes = Base64.decode(model.image, 0)
        return Celeb(
            name = model.name,
            image = bytes.toBitmap()
        )
    }
}