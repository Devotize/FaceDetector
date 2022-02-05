package com.sychev.camera.impl.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.feature.deafine.gender.api.CommonGender
import com.sychev.feature.define.gender.impl.DefineGenderUseCase
import com.sychev.feature.preferences.api.PreferencesManagerProviderApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CameraViewModel @Inject constructor(
    private val defineGenderUseCase: DefineGenderUseCase,
    private val prefs: PreferencesManagerProviderApi
): ViewModel() {

    val shouldShowCamera = prefs.isCameraPermissionGranted

    private val _definedGender = MutableSharedFlow<CommonGender>()
    val definedGender = _definedGender.asSharedFlow()

    val needStartJob = MutableSharedFlow<Unit>()

    private var processingJob: Job? = null

    init {
        viewModelScope.launch {
            _definedGender.collect {
                needStartJob.emit(Unit)
            }
        }
    }


    fun setCameraPermission(value: Boolean) {
        prefs.setIsCameraPermissionGranted(value = value)
    }

    fun startProcessingJob(bitmap: Bitmap) {
        stopProcessingJob()
        processingJob = viewModelScope.launch(Dispatchers.IO) {
            defineGenderUseCase.defineGender(bitmap).collect {
                _definedGender.emit(it)
            }
        }
        processingJob?.start()
    }

    private fun stopProcessingJob() {
        processingJob?.cancel()
        processingJob = null
    }

}