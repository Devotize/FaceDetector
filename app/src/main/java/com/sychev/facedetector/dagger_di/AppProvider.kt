package com.sychev.facedetector.dagger_di

import androidx.compose.runtime.compositionLocalOf
import com.sychev.common.Destinations
import com.sychev.common.di.CommonProvider
import com.sychev.feature.preferences.api.PreferencesProvider

interface AppProvider: CommonProvider, PreferencesProvider {
    val destinations: Destinations
}

val LocalAppProvider = compositionLocalOf<AppProvider> { error("No app provider found") }