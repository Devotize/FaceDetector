package com.sychev.facedetector.di

import com.sychev.facedetector.data.local.dao.DetectedClothesDao
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.mapper.DetectedClothesEntityConverter
import com.sychev.facedetector.data.local.mapper.SavedScreenshotConverter
import com.sychev.facedetector.data.remote.CelebDetectionApi
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.converter.DetectedClothesDtoConverter
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.repository.DetectedClothesRepositoryImpl
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.repository.SavedScreenshotRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun provideSavedScreenshotRepository(
        savedScreenshotConverter: SavedScreenshotConverter,
        screenshotDao: ScreenshotDao,
        celebDetectionApi: CelebDetectionApi,

    ): SavedScreenshotRepo {
        return SavedScreenshotRepoImpl(
            screenshotDao,
            savedScreenshotConverter,
            celebDetectionApi,

        )
    }

    @Singleton
    @Provides
    fun provideDetectedClothesRepository(
        clothesDetectionApi: ClothesDetectionApi,
        detectedClothesDao: DetectedClothesDao,
        detectedClothesEntityConverter: DetectedClothesEntityConverter,
        detectedClothesDtoConverter: DetectedClothesDtoConverter,
    ): DetectedClothesRepository {
        return DetectedClothesRepositoryImpl(
            clothesDetectionApi,
            detectedClothesDao,
            detectedClothesEntityConverter,
            detectedClothesDtoConverter,
        )
    }

}