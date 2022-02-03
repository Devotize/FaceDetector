package com.sychev.common.di

import com.sychev.common.PermissionManager
import dagger.Module
import dagger.Provides

@Module
object CommonModule {
    @Provides
    fun providePermissionManager() = PermissionManager()
}