package com.sychev.facedetector.presentation.ui.main

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sychev.facedetector.data.local.entity.ScreenshotEntity
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.repository.SavedScreenshotRepo
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
    val repository: SavedScreenshotRepo,
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
        }
    }

    private suspend fun getScreenshotsFromCache() {
        isLoading.value = true
        screenshotList.value = repository.getAllScreenshots()
        Log.d(TAG, "getScreenshotsFromCache: screenshotList.value = ${screenshotList.value}")
    }

}