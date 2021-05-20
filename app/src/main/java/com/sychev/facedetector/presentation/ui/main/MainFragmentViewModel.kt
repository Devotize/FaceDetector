package com.sychev.facedetector.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.registerForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.ViewModel
import com.sychev.facedetector.data.local.entity.ScreenshotEntity
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.presentation.MainActivity
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.service.FaceDetectorService
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel
@Inject
    constructor(
    private val repository: SavedScreenshotRepo,
): ViewModel() {

    val screenshotList: MutableState<List<SavedScreenshot>?> = mutableStateOf(null);
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    fun onTriggerEvent(event: MainEvent) {
        when (event) {
            is MainEvent.GetAllScreenshots -> {
                CoroutineScope(IO).launch {
                    getScreenshotsFromCache()
                }
            }
            is MainEvent.PerformGoogleSearch -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${event.celebName}"))
                event.context.startActivity(browserIntent)
            }
            is MainEvent.LaunchDetector -> {
                launchDetector(event.launcher)
            }
        }
    }

    private suspend fun getScreenshotsFromCache() {
        isLoading.value = true
        screenshotList.value = repository.getAllScreenshots()
        Log.d(TAG, "getScreenshotsFromCache: screenshotList.value = ${screenshotList.value}")
    }

    private fun launchDetector(launcher: MainActivity){
        launcher.startAssistantService()
    }

}

















