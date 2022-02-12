package com.sychev.feature.gallery.api

import com.sychev.common.BaseEntryPoint

abstract class GalleryEntryPoint : BaseEntryPoint() {

    final override val entryRoute: String
        get() = "gallery"
}