package com.sychev.feature.preferences.api

import androidx.compose.runtime.compositionLocalOf

interface PreferencesProvider {
    val prefs: PreferencesManagerProviderApi
}

val LocalPreferencesProvider = compositionLocalOf<PreferencesProvider> { error("No preferences provider found") }