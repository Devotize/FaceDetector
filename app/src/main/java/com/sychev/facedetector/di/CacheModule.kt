package com.sychev.facedetector.di

import android.content.Context
import androidx.room.Room
import com.sychev.facedetector.data.local.AppDatabase
import com.sychev.facedetector.data.local.mapper.DetectedClothesEntityConverter
import com.sychev.facedetector.data.local.mapper.SavedScreenshotConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    @Singleton
    fun provideAppDb(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideScreenshotDao(appDb: AppDatabase) = appDb.screenshotDao()

    @Provides
    @Singleton
    fun provideDetectedClothesDao(appDb: AppDatabase) = appDb.detectedClothesDao()

    @Singleton
    @Provides
    fun provideEntityMapper() = SavedScreenshotConverter()

    @Singleton
    @Provides
    fun provideDetectedClothesConverter() = DetectedClothesEntityConverter()

}








