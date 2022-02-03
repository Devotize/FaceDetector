package com.sychev.camera.impl.viewmodel

import androidx.lifecycle.ViewModel
import com.sychev.feature.define.gender.impl.DefineGenderUseCase
import com.sychev.feature.preferences.api.PreferencesManagerProviderApi
import javax.inject.Inject

class CameraViewModel @Inject constructor(
    private val defineGenderUseCase: DefineGenderUseCase,
    private val prefs: PreferencesManagerProviderApi
): ViewModel() {

    val shouldShowCamera = prefs.isCameraPermissionGranted

    fun setCameraPermission(value: Boolean) {
        prefs.setIsCameraPermissionGranted(value = value)
    }

}