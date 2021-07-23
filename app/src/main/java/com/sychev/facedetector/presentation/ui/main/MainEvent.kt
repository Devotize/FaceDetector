package com.sychev.facedetector.presentation.ui.main

import android.content.Context
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.activity.MainActivity

sealed class MainEvent() {
    object GetAllScreenshots: MainEvent()

    class PerformGoogleSearch(val context: Context, val celebName: String): MainEvent()

    class LaunchDetector(val launcher: MainActivity) : MainEvent()

    object GetAllDetectedClothes: MainEvent()

    class AddToFavoriteDetectedClothesEvent(val detectedClothes: DetectedClothes): MainEvent()

    class RemoveFromFavoriteDetectedClothesEvent(val detectedClothes: DetectedClothes): MainEvent()
}
