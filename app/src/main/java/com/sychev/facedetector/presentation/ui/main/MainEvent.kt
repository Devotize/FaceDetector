package com.sychev.facedetector.presentation.ui.main

import android.content.Context
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.activity.MainActivity

sealed class MainEvent() {
    object GetAllScreenshots: MainEvent()

    class PerformGoogleSearch(val context: Context, val celebName: String): MainEvent()

    class LaunchDetector(val launcher: MainActivity, val closeApp: Boolean) : MainEvent()

    object GetAllClothes: MainEvent()

    object GetAllFavoriteClothes: MainEvent()

    class AddToFavoriteClothesEvent(val clothes: Clothes): MainEvent()

    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): MainEvent()
}
