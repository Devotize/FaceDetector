package com.sychev.facedetector.dagger_di

import com.sychev.common.di.CommonProvider
import com.sychev.feature.preferences.api.PreferencesProvider
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    dependencies = [
        CommonProvider::class,
        PreferencesProvider::class,
    ],
    modules = [
        NavigationModule::class,
    ]
)
interface AppComponent: AppProvider