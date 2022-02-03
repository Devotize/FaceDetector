package com.sychev.feature.preferences.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.sychev.feature.preferences.api.PreferencesKeyManager
import com.sychev.feature.preferences.api.PreferencesManagerProviderApi
import com.sychev.feature.preferences.impl.PreferencesKeyManagerImpl
import com.sychev.feature.preferences.impl.PreferencesManager
import com.sychev.feature.preferences.impl.PreferencesManagerProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [PreferencesModule.Bindings::class])
object PreferencesModule {

    @Module(includes = [PreferencesModule::class])
    interface Bindings {
        @Binds
        @Singleton
        fun providePreferencesKeyManager(prefsKeyStoreImpl: PreferencesKeyManagerImpl): PreferencesKeyManager

    }

    @Provides
    @Singleton
    fun providePreferencesProviderImpl(
        preferencesKeyManager: PreferencesKeyManager,
        preferencesManager: PreferencesManager,
    ): PreferencesManagerProviderApi {
        return PreferencesManagerProviderImpl(
            preferencesKeyManager = preferencesKeyManager,
            preferencesManager = preferencesManager,
        )
    }

    @Provides
    @Singleton
    fun providerPreferencesManager(dataStore: DataStore<Preferences>): PreferencesManager =
        PreferencesManager(dataStore)

    @Provides
    @Singleton
    fun provideDataStore(context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("app_prefs")
        }



}