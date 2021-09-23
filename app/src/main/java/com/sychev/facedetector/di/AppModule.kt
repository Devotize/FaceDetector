package com.sychev.facedetector.di

import android.content.Context
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.presentation.BaseApplication
import com.sychev.facedetector.presentation.ui.detectorAssitant.AssistantManager
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorViewModel
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext context: Context): BaseApplication {
        return context as BaseApplication
    }

    @Provides
    fun provideViewModel(@ApplicationContext context: Context): DetectorViewModel
    {
        return DetectorViewModel(context)
    }

    @Singleton
    @Provides
    fun provideNavigationManager() = NavigationManager()

    @Singleton
    @Provides
    fun provideAssistantManager() = AssistantManager()

    @Singleton
    @Provides
    fun provideFilterValues() = FilterValues()
}