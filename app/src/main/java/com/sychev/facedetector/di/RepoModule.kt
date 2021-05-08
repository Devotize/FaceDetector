package com.sychev.facedetector.di

import com.sychev.facedetector.data.local.dao.ScreenshotDao
import com.sychev.facedetector.data.local.mapper.EntityMapper
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.repository.SavedScreenshotRepo_Impl
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
    fun provideRepository(
        entityMapper: EntityMapper,
        screenshotDao: ScreenshotDao,
    ): SavedScreenshotRepo {
        return SavedScreenshotRepo_Impl(
            screenshotDao, entityMapper
        )
    }

}