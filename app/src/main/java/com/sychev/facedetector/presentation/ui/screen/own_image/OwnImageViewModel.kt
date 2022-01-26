package com.sychev.facedetector.presentation.ui.screen.own_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.ImageData
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.interactors.image.GetImagesFromCache
import com.sychev.facedetector.interactors.image.InsertImageToCache
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.feed_list.FoundedClothes
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OwnImageViewModel @Inject constructor(
    private val detectClothesLocal: DetectClothesLocal,
    private val searchClothes: SearchClothes,
    private val insertImageToCache: InsertImageToCache,
    private val getImagesFromCache: GetImagesFromCache,
    private val navigationManager: NavigationManager,
): ViewModel() {

    var imageUri = mutableStateOf<Uri?>(null)
    val foundedClothes = mutableStateListOf<WrappedClothes>()
    val detectedClothes = mutableStateListOf<DetectedClothes>()
    val isImageAlreadyProcessed = mutableStateOf(false)
    val selectedClothesList = mutableStateOf<WrappedClothes?>(null)
    val images = mutableStateListOf<WrappedImageData>()
    val selectedImageIndex = mutableStateOf(0)


    fun onTriggerEvent(event: OwnImageEvent) {
        when (event) {
            is OwnImageEvent.CreateImageUri -> {
                createUri(event.context, createdUri = event.createdUri)
            }
            is OwnImageEvent.DetectClothesLocal -> {
                detectedClothesLocal(event.context, event.croppedBitmap, event.wrappedImageData)
            }
            is OwnImageEvent.SearchClothes -> {
                searchClothes(event.context, event.detectedClothes, event.wrappedImageData)
            }
            is OwnImageEvent.GoToRetailScreen -> {
                val retailScreen = Screen.ClothesListRetail.apply {
                    arguments = arrayListOf<Parcelable>().apply {
                        addAll(detectedClothes)
                    }
                }
                navigationManager.navigate(retailScreen)
            }
            is OwnImageEvent.OnGenderChange -> {
                onGenderChange(event.newGender, event.imageData, event.clothes, event.context)
            }
            is OwnImageEvent.InsertBitmapInCache -> {
                insertImageToCache(event.bitmap, event.context)
            }
            is OwnImageEvent.GetImagesFromCache -> {
                getImagesFromCache(event.context, event.hasImages)
            }
        }
    }
    fun onInit(context: Context, image: Bitmap) {
        onTriggerEvent(OwnImageEvent.GetImagesFromCache(context){ hasImages ->
            if (!hasImages) {
                onTriggerEvent(OwnImageEvent.InsertBitmapInCache(image, context))
            }
        })
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

    private fun detectedClothesLocal(context: Context, croppedBitmap: Bitmap, imageData: WrappedImageData) {
        detectClothesLocal.execute(context, croppedBitmap).onEach { dataState ->
            isImageAlreadyProcessed.value = true
            detectedClothes.clear()
            foundedClothes.clear()
            dataState.data?.let {
                if (it.isNotEmpty()) {
                    detectedClothes.addAll(it)
                    it.forEach { dc ->
                        searchClothes(context, dc, imageData)
                    }
                } else {
                    imageData.showNothingFound = true
                    images.add(imageData)
                    images.removeLast()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun searchClothes(context: Context, detectedClothes: DetectedClothes, imageData: WrappedImageData) {
        searchClothes.execute(detectedClothes, context).onEach { dataState ->
            dataState.data?.let {
                Log.d(TAG, "searchClothes: smthng found")
                if (it.isNotEmpty()) {
                    imageData.clothes.add(
                        WrappedClothes(
                            detectedClothes = detectedClothes,
                            clothes = it
                        )
                    )
                    imageData.showNothingFound = false
                    images.add(imageData)
                    images.removeLast()
                } else {
                    if (imageData.clothes.isEmpty()) {
                        imageData.showNothingFound = true
                    }
                }
                images.add(imageData)
                images.removeLast()
            }
            dataState.error?.let {
                Log.d(TAG, "searchClothes: error: ${it}")
            }
        }.launchIn(viewModelScope)
    }

    fun onSelectedClothesChange(imageData: WrappedImageData, newClothes: WrappedClothes?) {
        imageData.selectedClothes = newClothes
        images.add(imageData)
        images.removeLast()
    }

    fun onGenderChange(newGender: String, imageData: WrappedImageData, clothes: WrappedClothes, context: Context) {
        val dc = clothes.detectedClothes.apply {
            gender = newGender
        }

        searchClothes.execute(dc, context).onEach { dataState ->
            dataState.data?.let {
                if (it.isNotEmpty()) {
                    Log.d(TAG, "onGenderChange: foundSomeNewClothes")
                    onSelectedClothesChange(
                        imageData,
                        WrappedClothes(
                            clothes = it,
                            detectedClothes = dc,
                        )
                    )
                }else {
                    Log.d(TAG, "onGenderChange: foundNothingNew")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun insertImageToCache(image: Bitmap, context: Context) {
        Log.d(TAG, "insertImageToCache: called")
        viewModelScope.launch {
            withContext(IO) {
                insertImageToCache.execute(image, context).collect { dataState ->
                    dataState.data?.let {
                        Log.d(TAG, "insertImageToCache: imageInserted")
                        images.add(
                            WrappedImageData(it)
                        )
                        selectedImageIndex.value = images.lastIndex
                    }
                }
            }
        }
    }

    private fun getImagesFromCache(context: Context, hasImages: (Boolean) -> Unit) {
        Log.d(TAG, "getImagesFromCache: called")
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                getImagesFromCache.execute(context).collect { dataState ->
                    dataState.data?.let {
                        Log.d(TAG, "getImagesFromCache: listimage data: $it")
                        if (it.isNotEmpty()) {
                            images.clear()
                            images.addAll(it.map { WrappedImageData(it) })
                            selectedImageIndex.value = it.lastIndex
                            hasImages(true)
                        } else {
                            hasImages(false)
                        }

                    }
                }
            }
        }
    }

    fun onGoToNextImage() {
        selectedImageIndex.value++
    }

    fun onGoToPreviousImage() {
        selectedImageIndex.value--
    }

}

data class WrappedClothes(
    val clothes: List<Clothes>,
    val detectedClothes: DetectedClothes,
)

data class WrappedImageData(
    val imageData: ImageData,
    var isProcessed: Boolean = false,
    var showNothingFound: Boolean = false,
    val clothes: ArrayList<WrappedClothes> = arrayListOf<WrappedClothes>(),
    var selectedClothes: WrappedClothes? = null,
)








