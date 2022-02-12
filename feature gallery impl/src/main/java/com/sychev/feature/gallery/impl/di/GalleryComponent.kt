package com.sychev.feature.gallery.impl.di

import com.sychev.common.di.EntryScoped
import com.sychev.feature.gallery.api.GalleryProvider
import com.sychev.feature.gallery.impl.viewmodel.GalleryViewModel
import dagger.Component

@Component
@EntryScoped
interface GalleryComponent : GalleryProvider {
    val viewModel: GalleryViewModel
}