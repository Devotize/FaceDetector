package com.sychev.facedetector.presentation.activity.main

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.interactors.clothes.GetClothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.interactors.detected_clothes.ClearDetectedClothes
import com.sychev.facedetector.interactors.detected_clothes.GetDetectedClothes
import com.sychev.facedetector.interactors.filter.GetFilterValues
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
    constructor(
    private val repository: SavedScreenshotRepo,
    private val getClothes: GetClothes,
    private val insertClothesToFavorite: InsertClothesToFavorite,
    private val removeFromFavoriteClothes: RemoveFromFavoriteClothes,
    private val getFilterValues: GetFilterValues,
    private val getDetectedClothes: GetDetectedClothes,
    private val clearDetectedClothes: ClearDetectedClothes,
    private val navigationManager: NavigationManager,
    ): ViewModel() {

    val screenshotList: MutableState<List<SavedScreenshot>?> = mutableStateOf(null);
    val loading: MutableState<Boolean> = mutableStateOf(false)
    val query = mutableStateOf("")
    val savedClothesList = mutableStateListOf<Clothes>()
    val launchFromAssistant = mutableStateOf(false)
    var closeApp = false
    val detectedClothesList = mutableStateListOf<DetectedClothes>()


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
                closeApp = event.closeApp
                launchDetector(event.launcher)
            }
            is MainEvent.GetAllClothes -> {
                getAllClothes()
            }
            is MainEvent.AddToFavoriteClothesEvent -> {
                addToFavoriteClothes(event.clothes)
            }
            is MainEvent.RemoveFromFavoriteClothesEvent -> {
                removeFromFavoriteClothes(event.clothes)
            }
            is MainEvent.GetAllFavoriteClothes -> {
                getAllFavoriteClothes()
            }
            is MainEvent.GetFilterValues -> {
                getFilterValues.execute().launchIn(viewModelScope)
            }
            is MainEvent.GetDetectedClothesEvent -> {
                getDetectedClothes()
            }
            is MainEvent.ClearDetectedClothesEvent -> {
                clearDetectedClothes()
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

    private fun getAllClothes() {
        getClothes.execute(false).onEach { dataState ->
           loading.value = dataState.loading
            dataState.data?.let {
                savedClothesList.clear()
                savedClothesList.addAll(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun getAllFavoriteClothes() {
        getClothes.execute(true).onEach { dataState ->
            loading.value = dataState.loading
            dataState.data?.let {
                savedClothesList.clear()
                savedClothesList.addAll(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun removeFromFavoriteClothes(clothes: Clothes) {
        Log.d(TAG, "removeFromFavoriteClothes: called")
        removeFromFavoriteClothes.execute(clothes)
            .onEach {
                if (!it.loading) {
                    savedClothesList.remove(clothes)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addToFavoriteClothes(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes)
            .onEach {
                if (!it.loading) {
                    getAllFavoriteClothes()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getDetectedClothes() {
        getDetectedClothes.execute().onEach { dataState ->
            dataState.data?.let {
                if (it.isNotEmpty()) {
                    detectedClothesList.addAll(it)
                    goToRetailScreen(it)
                    clearDetectedClothes()
                }
            }
        }.launchIn(CoroutineScope(Main))
    }

    private fun clearDetectedClothes() {
        clearDetectedClothes.execute().onEach {

        }.launchIn(viewModelScope)
    }

    private fun goToRetailScreen(detectedClothes: List<DetectedClothes>) {
        Log.d(TAG, "goToRetailScreen: detectedClothes: $detectedClothes")
        val retailScreen = Screen.ClothesListRetail.apply {
            arguments = arrayListOf<Parcelable>().apply {
                addAll(detectedClothes)
            }
        }
        navigationManager.navigate(retailScreen)
    }

}

















