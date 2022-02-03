package com.sychev.feature.preferences.api

import androidx.datastore.preferences.core.Preferences

interface PreferencesKeyManager {
    val CAMERA_PERMISSION: Preferences.Key<Boolean>
}