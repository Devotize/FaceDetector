package com.sychev.feature.preferences.impl.di

import com.sychev.common.di.CommonProvider
import com.sychev.feature.preferences.api.PreferencesProvider
import dagger.Component
import javax.inject.Singleton

@Component(
    dependencies = [CommonProvider::class],
    modules = [PreferencesModule::class]
)
@Singleton
interface PreferencesComponent: PreferencesProvider {
}