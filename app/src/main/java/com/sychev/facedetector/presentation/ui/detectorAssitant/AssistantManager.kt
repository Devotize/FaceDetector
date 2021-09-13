package com.sychev.facedetector.presentation.ui.detectorAssitant

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssistantManager {
    private val _isAssistantActive = MutableStateFlow<Boolean>(false)
    val isAssistantActive = _isAssistantActive.asStateFlow()

    fun onActiveStatusChange(newStatus: Boolean) {
        _isAssistantActive.value = newStatus
    }
}