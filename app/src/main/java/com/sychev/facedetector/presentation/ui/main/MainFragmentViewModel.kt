package com.sychev.facedetector.presentation.ui.main

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.interactors.clothes.GetClothesList
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.presentation.MainActivity
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel
@Inject
    constructor(
    private val repository: SavedScreenshotRepo,
    private val getClothesList: GetClothesList,
    private val insertClothesToFavorite: InsertClothesToFavorite,
    private val removeFromFavoriteClothes: RemoveFromFavoriteClothes,
): ViewModel() {

    val screenshotList: MutableState<List<SavedScreenshot>?> = mutableStateOf(null);
    val loading: MutableState<Boolean> = mutableStateOf(false)
    val query = mutableStateOf("")
    val detectedClothesList = mutableStateListOf<DetectedClothes>()
    val hugeFirstElement = mutableStateOf(false)

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
            is MainEvent.GetAllDetectedClothes -> {
                getAllDetectedClothes()
            }
            is MainEvent.AddToFavoriteDetectedClothesEvent -> {
                addToFavoriteClothes(event.detectedClothes)
            }
            is MainEvent.RemoveFromFavoriteDetectedClothesEvent -> {
                removeFromFavoriteClothes(event.detectedClothes)
            }
        }
    }

    private suspend fun getScreenshotsFromCache() {
        loading.value = true
        screenshotList.value = repository.getAllScreenshots()
        Log.d(TAG, "getScreenshotsFromCache: screenshotList.value = ${screenshotList.value}")
    }

    private fun launchDetector(launcher: MainActivity){
        launcher.startAssistantService()
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    private fun getAllDetectedClothes() {
        getClothesList.execute().onEach { dataState ->
           loading.value = dataState.loading
            dataState.data?.let {
                Log.d(TAG, "getAllDetectedClothes: detectedClothes: $it")
                detectedClothesList.clear()
                detectedClothesList.addAll(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun removeFromFavoriteClothes(detectedClothes: DetectedClothes) {
        Log.d(TAG, "removeFromFavoriteClothes: called")
        removeFromFavoriteClothes.execute(detectedClothes)
            .onEach {
                if (!it.loading) {
                    getAllDetectedClothes()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addToFavoriteClothes(detectedClothes: DetectedClothes) {
        insertClothesToFavorite.execute(detectedClothes)
            .onEach {
                if (!it.loading) {
                    getAllDetectedClothes()
                }
            }
            .launchIn(viewModelScope)
    }

}

















