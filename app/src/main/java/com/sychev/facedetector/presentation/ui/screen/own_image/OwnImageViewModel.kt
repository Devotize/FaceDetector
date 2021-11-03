package com.sychev.facedetector.presentation.ui.screen.own_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Parcelable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.feed_list.FoundedClothes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OwnImageViewModel @Inject constructor(
    private val detectClothesLocal: DetectClothesLocal,
    private val searchClothes: SearchClothes,
    private val navigationManager: NavigationManager,
): ViewModel() {

    var imageUri = mutableStateOf<Uri?>(null)
    val foundedClothes = mutableStateListOf<FoundedClothes>()
    val detectedClothes = mutableStateListOf<DetectedClothes>()
    val isImageAlreadyProcessed = mutableStateOf(false)

    fun onTriggerEvent(event: OwnImageEvent) {
        when (event) {
            is OwnImageEvent.CreateImageUri -> {
                createUri(event.context, createdUri = event.createdUri)
            }
            is OwnImageEvent.DetectClothesLocal -> {
                detectedClothesLocal(event.context, event.croppedBitmap)
            }
            is OwnImageEvent.SearchClothes -> {
                searchClothes(event.context, event.detectedClothes)
            }
            is OwnImageEvent.GoToRetailScreen -> {
                val retailScreen = Screen.ClothesListRetail.apply {
                    arguments = arrayListOf<Parcelable>().apply {
                        addAll(detectedClothes)
                    }
                }
                navigationManager.navigate(retailScreen)
            }
        }
    }

    private fun createUri(context: Context, createdUri: (Uri) -> Unit) {
        val uri = FileProvider.getUriForFile(context, "com.sychev.facedetector.provider", createImageFile(context))
        imageUri.value = null
        imageUri.value = uri
        createdUri(uri)
    }

    private fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat.getDateTimeInstance().format(Date())
        val storageDir = context.getExternalFilesDir(DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun detectedClothesLocal(context: Context, croppedBitmap: Bitmap) {
        detectClothesLocal.execute(context, croppedBitmap).onEach { dataState ->
            isImageAlreadyProcessed.value = true
            detectedClothes.clear()
            foundedClothes.clear()
            dataState.data?.let {
                detectedClothes.addAll(it)
                it.forEach { dc ->
                    searchClothes(context, dc)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun searchClothes(context: Context, detectedClothes: DetectedClothes) {
        searchClothes.execute(detectedClothes, context).onEach { dataState ->
            dataState.data?.let {
                foundedClothes.add(
                    FoundedClothes(
                        location = detectedClothes.location,
                        clothes = it
                ))
            }
        }.launchIn(viewModelScope)
    }

}








