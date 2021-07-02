package com.sychev.facedetector.di

import com.sychev.facedetector.data.local.dao.DetectedClothesDao
import com.sychev.facedetector.data.local.mapper.DetectedClothesEntityConverter
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.converter.DetectedClothesDtoConverter
import com.sychev.facedetector.interactors.clothes.DeleteClothes
import com.sychev.facedetector.interactors.clothes.GetClothesList
import com.sychev.facedetector.interactors.clothes.GetFavoriteClothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.repository.DetectedClothesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InteractorModule {

    @Provides
    fun provideSearchClothes(
        detectedClothesRepository: DetectedClothesRepository
    ): SearchClothes {
        return SearchClothes(
            detectedClothesRepository
        )
    }

    @Provides
    fun provideInsertClothesToFavorite(
        detectedClothesRepository: DetectedClothesRepository
    ): InsertClothesToFavorite {
        return InsertClothesToFavorite(
            detectedClothesRepository
        )
    }

    @Provides
    fun provideGetFavoriteClothes(
        detectedClothesRepository: DetectedClothesRepository
    ): GetFavoriteClothes {
        return GetFavoriteClothes(detectedClothesRepository)
    }

    @Provides
    fun provideDeleteClothes(
        detectedClothesRepository: DetectedClothesRepository
    ): DeleteClothes {
        return DeleteClothes(detectedClothesRepository)
    }

    @Provides
    fun provideGetClothesList(
        detectedClothesRepository: DetectedClothesRepository
    ): GetClothesList {
        return GetClothesList(detectedClothesRepository)
    }

    @Provides
    fun provideDetectClothesLocal(): DetectClothesLocal {
        return DetectClothesLocal()
    }


}