package com.sychev.facedetector.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sychev.facedetector.data.local.AppDatabase
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
            ).build()
    }

    @Provides
    @Singleton
    fun provideScreenshotDao(appDb: AppDatabase) = appDb.screenshotDao()

}