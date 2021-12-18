package com.sychev.facedetector.data.local.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.sychev.facedetector.data.local.entity.ScreenshotEntity
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toByteArray
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class SavedScreenshotConverter: DomainMapper<SavedScreenshot, ScreenshotEntity> {
    fun fromDomainModel(model: SavedScreenshot): ScreenshotEntity {
        return ScreenshotEntity(
            image = model.image.toByteArray(),
            celebName = model.celebName
        )
    }

    override fun toDomainModel(model: ScreenshotEntity): SavedScreenshot {
        return SavedScreenshot(
            model.id,
            model.image.toBitmap(),
            model.celebName
        )
    }

    fun toDomainList(list: List<ScreenshotEntity>): List<SavedScreenshot>{
        return list.map{
            SavedScreenshot(
                it.id,
                it.image.toBitmap(),
                it.celebName
            )
        }
    }

}