package com.sychev.facedetector.repository

import android.graphics.Bitmap
import com.sychev.facedetector.data.local.dao.ImageDataDao
import com.sychev.facedetector.data.local.mapper.ImageDataEntityConverter

class ImageDataRepositoryImpl(
    private val imageDataEntityConverter: ImageDataEntityConverter = ImageDataEntityConverter(),
    private val imageDataDao: ImageDataDao,
): ImageDataRepository {
    override suspend fun insertImageToCache(image: Bitmap): Long {
        return imageDataDao.insertImageData(imageDataEntityConverter.fromDomainModel(image))
    }

    override suspend fun getAllImages(): List<Bitmap> {
        return imageDataDao.getAllImageData().map { imageDataEntityConverter.toDomainModel(it) }
    }
}