package com.sychev.camera.impl.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.camera.impl.model.DetectedClothesWithGender
import com.sychev.feature.define.clothes.impl.DefineClothesUseCase
import com.sychev.feature.define.gender.impl.DefineGenderUseCase
import com.sychev.feature.preferences.api.PreferencesManagerProviderApi
import com.sychev.utils.logXertz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class CameraViewModel @Inject constructor(
    private val defineGenderUseCase: DefineGenderUseCase,
    private val defineClothesUseCase: DefineClothesUseCase,
    private val prefs: PreferencesManagerProviderApi
): ViewModel() {

    val shouldShowCamera = prefs.isCameraPermissionGranted

    private val _detectedClothesWithGender = MutableSharedFlow<DetectedClothesWithGender>()
    val detectedClothesWithGender = _detectedClothesWithGender.asSharedFlow()

    val needStartJob = MutableSharedFlow<Unit>()

    private var processingJob: Job? = null

    init {
        viewModelScope.launch {
            _detectedClothesWithGender.collect {
                logXertz("detectedClothes with gender: $it")
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
            combine(
                defineGenderUseCase.defineGender(bitmap),
                defineClothesUseCase.defineClothes(bitmap),
            ) { gender, clothes ->
                DetectedClothesWithGender(
                    detectedClothes = clothes,
                    gender = gender,
                )
            }.collect {
                logXertz("combined detections: $it")
                _detectedClothesWithGender.emit(
                    it
                )
            }
        }

        processingJob?.start()
    }

    private fun stopProcessingJob() {
        processingJob?.cancel()
        processingJob = null
    }

}