package com.sychev.facedetector.presentation

import android.app.Application
import com.sychev.common.di.DaggerCommonComponent
import com.sychev.facedetector.dagger_di.AppProvider
import com.sychev.facedetector.dagger_di.DaggerAppComponent
import com.sychev.feature.preferences.impl.di.DaggerPreferencesComponent
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication: Application() {

    lateinit var appProvider: AppProvider

    override fun onCreate() {
        super.onCreate()

        val commonProvider = DaggerCommonComponent.factory().create(this)
        val preferencesProvider = DaggerPreferencesComponent.builder().commonProvider(commonProvider).build()
        appProvider = DaggerAppComponent.builder()
            .commonProvider(commonProvider)
            .preferencesProvider(preferencesProvider)
            .build()
    }
}

val Application.appProvider: AppProvider
 get() = (this as BaseApplication).appProvider