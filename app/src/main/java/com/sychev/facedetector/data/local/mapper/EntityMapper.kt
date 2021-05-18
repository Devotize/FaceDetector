package com.sychev.facedetector.data.local.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.sychev.facedetector.data.local.entity.ScreenshotEntity
import com.sychev.facedetector.domain.DomainMapper
import com.sychev.facedetector.domain.SavedScreenshot
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class EntityMapper: DomainMapper<SavedScreenshot, ScreenshotEntity> {
    override fun fromDomainModel(model: SavedScreenshot): ScreenshotEntity {
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

    private fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

}