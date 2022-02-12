package com.sychev.feature.gallery.impl.di

import com.sychev.common.EntryPoint
import com.sychev.common.di.EntryPointKey
import com.sychev.feature.gallery.api.GalleryEntryPoint
import com.sychev.feature.gallery.impl.ui.GalleryEntryPointImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface GalleryEntryModule {

    @Binds
    @Singleton
    @IntoMap
    @EntryPointKey(GalleryEntryPoint::class)
    fun bindGalleryEntryPoint(entryPoint: GalleryEntryPointImpl): EntryPoint

}