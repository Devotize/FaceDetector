package com.sychev.facedetector.presentation.ui.main

import android.content.Context
import com.sychev.facedetector.presentation.MainActivity

sealed class MainEvent() {
    object GetAllScreenshots: MainEvent()

    class PerformGoogleSearch(val context: Context, val celebName: String): MainEvent()

    class LaunchDetector(val launcher: MainActivity) : MainEvent()
}
