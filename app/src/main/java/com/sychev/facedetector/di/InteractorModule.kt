package com.sychev.facedetector.di

import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.interactors.clothes.*
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.interactors.clothes_list.ProcessClothesForRetail
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.interactors.filter.GetFilterValues
import com.sychev.facedetector.interactors.gender.DefineGender
import com.sychev.facedetector.interactors.pics.GetCelebPics
import com.sychev.facedetector.interactors.pics.GetRandomPics
import com.sychev.facedetector.repository.DetectedClothesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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

    @Provides
    fun provideRemoveFromFavoriteClothes(
        detectedClothesRepository: DetectedClothesRepository
    ) = RemoveFromFavoriteClothes(detectedClothesRepository)

    @Provides
    fun provideDefineGender(): DefineGender = DefineGender()

    @Provides
    fun provideGetRandomPics(
        detectedClothesRepository: DetectedClothesRepository
    ): GetRandomPics = GetRandomPics(detectedClothesRepository)

    @Provides
    fun provideGetCelebPics(
        detectedClothesRepository: DetectedClothesRepository
    ): GetCelebPics = GetCelebPics(detectedClothesRepository)

    @Provides
    fun provideProcessClothesOfrRetail(
        detectedClothesRepository: DetectedClothesRepository
    ): ProcessClothesForRetail = ProcessClothesForRetail(detectedClothesRepository)

    @Provides
    fun provideGetFilterValues(
        clothesRepository: DetectedClothesRepository,
        filterValues: FilterValues
    ): GetFilterValues = GetFilterValues(clothesRepository, filterValues)

}