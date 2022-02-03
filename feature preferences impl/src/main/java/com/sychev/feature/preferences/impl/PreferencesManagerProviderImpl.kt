package com.sychev.feature.preferences.impl

import com.sychev.feature.preferences.api.PreferencesKeyManager
import com.sychev.feature.preferences.api.PreferencesManagerProviderApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesManagerProviderImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val preferencesKeyManager: PreferencesKeyManager
): PreferencesManagerProviderApi {

    //setters
    override fun setIsCameraPermissionGranted(value: Boolean) {
        preferencesManager.setPrefs(preferencesKeyManager.CAMERA_PERMISSION, value)
    }

    //getters
    override val isCameraPermissionGranted: Flow<Boolean> =
        preferencesManager.getPrefs(preferencesKeyManager.CAMERA_PERMISSION, false)
}