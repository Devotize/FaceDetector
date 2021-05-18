package com.sychev.facedetector.presentation.ui.main

import android.content.Context

sealed class MainEvent() {
    object GetAllScreenshots: MainEvent()

    class PerformGoogleSearch(val context: Context, val celebName: String): MainEvent()

    object LaunchDetector : MainEvent()
}
