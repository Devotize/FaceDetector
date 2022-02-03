package com.sychev.feature.preferences.impl

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.sychev.feature.preferences.api.PreferencesKeyManager
import javax.inject.Inject

class PreferencesKeyManagerImpl @Inject constructor(): PreferencesKeyManager {
    override val CAMERA_PERMISSION: Preferences.Key<Boolean>
        get() = booleanPreferencesKey("CAMERA_PERMISSION_KEY")

}