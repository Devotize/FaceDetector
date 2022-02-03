package com.sychev.feature.preferences.api

import kotlinx.coroutines.flow.Flow

interface PreferencesManagerProviderApi {
    fun setIsCameraPermissionGranted(value: Boolean)

    val isCameraPermissionGranted: Flow<Boolean>
}