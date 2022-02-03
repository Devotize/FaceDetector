package com.sychev.camera.impl.di

import com.sychev.camera.api.CameraEntryPoint
import com.sychev.camera.impl.ui.CameraEntryPointImpl
import com.sychev.common.EntryPoint
import com.sychev.common.di.EntryPointKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface CameraEntryModule {

    @Binds
    @Singleton
    @IntoMap
    @EntryPointKey(CameraEntryPoint::class)
    fun cameraEntry(entryPoint: CameraEntryPointImpl): EntryPoint

}