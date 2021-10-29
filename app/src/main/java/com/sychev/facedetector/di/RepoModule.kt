package com.sychev.facedetector.di

import com.sychev.facedetector.data.local.dao.ClothesDao
import com.sychev.facedetector.data.local.dao.DetectedClothesDao
import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.mapper.ClothesEntityConverter
import com.sychev.facedetector.data.local.mapper.DetectedClothesEntityConverter
import com.sychev.facedetector.data.local.mapper.SavedScreenshotConverter
import com.sychev.facedetector.data.remote.CelebDetectionApi
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.UnsplashApi
import com.sychev.facedetector.data.remote.converter.BrandDtoConverter
import com.sychev.facedetector.data.remote.converter.ClothesDtoConverter
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
    fun provideClothesRepository(
        clothesDetectionApi: ClothesDetectionApi,
        clothesDao: ClothesDao,
        detectedClothesDao: DetectedClothesDao,
        unsplashApi: UnsplashApi,
        clothesEntityConverter: ClothesEntityConverter,
        clothesDtoConverter: ClothesDtoConverter,
        brandDtoConverter: BrandDtoConverter,
        detectedClothesEntityConverter: DetectedClothesEntityConverter
    ): DetectedClothesRepository {
        return DetectedClothesRepositoryImpl(
            clothesDetectionApi,
            clothesDao,
            detectedClothesDao,
            unsplashApi,
            clothesEntityConverter,
            clothesDtoConverter,
            brandDtoConverter,
            detectedClothesEntityConverter
        )
    }



}