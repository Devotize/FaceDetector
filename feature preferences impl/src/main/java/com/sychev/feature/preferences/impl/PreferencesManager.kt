package com.sychev.feature.preferences.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.sychev.utils.defaultLaunch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    fun <T> setPrefs(key: Preferences.Key<T>, value: T) {
        defaultLaunch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun <T> getPrefs(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }




}