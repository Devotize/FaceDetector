package com.sychev.facedetector.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sychev.facedetector.data.local.AppDatabase
import com.sychev.facedetector.data.local.mapper.EntityMapper
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

    @Singleton
    @Provides
    fun provideEntityMapper() = EntityMapper()

}








