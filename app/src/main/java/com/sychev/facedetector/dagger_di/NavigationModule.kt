package com.sychev.facedetector.dagger_di

import com.sychev.camera.impl.di.CameraEntryModule
import com.sychev.feature.gallery.impl.di.GalleryEntryModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module(
    includes = [
        CameraEntryModule::class,
        GalleryEntryModule::class,
    ]
)
interface NavigationModule