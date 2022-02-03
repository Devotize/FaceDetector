package com.sychev.camera.impl.di

import com.sychev.camera.api.CameraProvider
import com.sychev.camera.impl.viewmodel.CameraViewModel
import com.sychev.common.di.EntryScoped
import com.sychev.feature.define.gender.impl.di.DefineGenderComponent
import com.sychev.feature.preferences.api.PreferencesProvider
import dagger.Component

@EntryScoped
@Component(
    dependencies = [
        PreferencesProvider::class,
        DefineGenderComponent::class,
                   ],
)
interface CameraComponent: CameraProvider, PreferencesProvider, DefineGenderComponent {
    val viewModel: CameraViewModel
}