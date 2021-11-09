package com.sychev.facedetector.presentation.ui.screen.feed_list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.interactors.pics.GetCelebPics
import com.sychev.facedetector.interactors.pics.GetRandomPics
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.utils.MessageDialog
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
@Inject
constructor(
    private val getRandomPics: GetRandomPics,
    private val detectClothesLocal: DetectClothesLocal,
    private val searchClothes: SearchClothes,
    private val getCelebPics: GetCelebPics,
    private val navigationManager: NavigationManager,
): ViewModel() {
    val urls = mutableStateListOf<String>()
    val loading = mutableStateOf(false)
    val celebImages = mutableStateOf<ArrayList<CelebImage>>(arrayListOf())
    var page = 0
    var lastVisibleIndex = 0

    init {
//        val accessKey = "6mDdwXvOc4qruhG8SxW889oVVTKd5VUESYAQrYXKnTE"
//        onTriggerEvent(FeedEvent.GetRandomPicsEvent(
//            accessKey = accessKey,
//            query = "man",
//            count = 25
//        ))
        onTriggerEvent(FeedEvent.GetCelebPicsEvent)
    }

    fun onTriggerEvent(event: FeedEvent) {
        when (event) {
            is FeedEvent.GetRandomPicsEvent -> {
                getRandomPics.execute(
                    event.accessKey,
                    event.query,
                    event.count
                ).onEach { dataState ->
                    loading.value = dataState.loading
                    dataState.data?.let{
                        urls.addAll(it)
                    }
                }.launchIn(viewModelScope)
            }
            is FeedEvent.DetectClothesEvent -> {
                detectClothes(
                    context = event.context,
                    resizedBitmap = event.resizedBitmap,
                    celebImage = event.celebImage,
                    callback = event.onLoaded
                )
            }
            is FeedEvent.FindClothes -> {
                searchClothes(
                    celebImage = event.celebImage,
                    detectedClothes = event.detectedClothes,
                    context = event.context,
                    location = event.location,
                    callback = event.onLoaded
                )
            }
            is FeedEvent.FindMultiplyClothes -> {
                searchMultiplyClothes(
                    celebImage =  event.celebImage,
                    context = event.context,
                    location = event.location,
                    callback = event.onLoaded
                )
            }
            is FeedEvent.GetCelebPicsEvent -> {
                Log.d(TAG, "onTriggerEvent: getCelebPicsEvent called")
                getCelebPics()
            }
            is FeedEvent.GoToRetailScreen -> {
                val retailScreen = Screen.ClothesListRetail.apply {
                    arguments = arrayListOf<Parcelable>().apply {
                        addAll(event.clothesList)
                    }
                }
                navigationManager.navigate(retailScreen)
            }
        }
    }

    private fun getCelebPics() {
        loading.value = true
        getCelebPics.execute(page).onEach { dataState ->
            loading.value = dataState.loading
            dataState.data?.let{ celebs ->
                val mappedCelebs =  celebs.map { CelebImage(image = it.image) }
                celebImages.value.addAll(mappedCelebs)
                // api pagination not working for now
//                page++
            }
        }.launchIn(viewModelScope)
    }

    fun onScrollPositionChanged(newPosition: Int) {
        Log.d(TAG, "onScrollPositionChanged: newPosition: $newPosition")
        lastVisibleIndex = newPosition
    }

    private fun searchMultiplyClothes(
        celebImage: CelebImage,
        context: Context,
        location: RectF,
        callback: (Boolean) -> Unit
    ) {
        searchClothes.execute(detectedClothesList = celebImage.detectedClothes, context = context)
            .onEach { dataState ->
                callback(dataState.loading)
                dataState.data?.let {
                    val fc = FoundedClothes(
                        location = location,
                        clothes = it
                    )
                    celebImages.value.forEachIndexed{index, ci ->
                        if (ci == celebImage) {
                            celebImages.value[index].foundedClothes.add(fc)
                            refreshCelebImagesData()
                        }
                    }
                    Log.d(TAG, "searchMultiplyClothes: foundedClothes: $fc")
                }
//
            }.launchIn(CoroutineScope(IO))
    }

    private fun detectClothes(context: Context, resizedBitmap: Bitmap, celebImage: CelebImage, callback: (Boolean) -> Unit) {
        detectClothesLocal.execute(context = context, bitmap = resizedBitmap)
            .onEach {dataState ->
                callback(dataState.loading)
                dataState.data?.let{
                    celebImages.value.forEachIndexed() { index, ci ->
                        if (ci == celebImage) {
                            celebImages.value[index].detectedClothes.addAll(it)
                            celebImages.value[index].isProcessed = true
                            refreshCelebImagesData()
                            it.forEach {
                                searchClothes(
                                    celebImages.value[index],
                                    detectedClothes = it,
                                    context = context,
                                    location = it.location,
                                    callback = {

                                    }
                                )
                            }
                        }
                    }
                }
            }.launchIn(CoroutineScope(IO))
    }

    private fun searchClothes(celebImage: CelebImage, detectedClothes: DetectedClothes, context: Context, location: RectF, callback: (Boolean?) -> Unit) {
        searchClothes.execute(detectedClothes = detectedClothes, context = context)
            .onEach { dataState ->
                callback(dataState.loading)
                dataState.data?.let {
                    val fc = FoundedClothes(
                        location = location,
                        clothes = it
                    )
                    celebImages.value.forEachIndexed{index, ci ->
                        if (ci == celebImage) {
                            celebImages.value[index].foundedClothes.add(fc)
                            refreshCelebImagesData()
                        }
                    }
                }
                dataState.error?.let { message ->
                    Log.d(TAG, "searchClothes: error: $message")
//                    callback(null)
//                    MessageDialog.dialogMessages.add(
//                        MessageDialog.Builder()
//                            .message("Похожей одежды не найдено")
//                            .title("Ошибка")
//                            .onDismiss {
//                                MessageDialog.dialogMessages.removeLast()
//                            }
//                            .onPositiveAction {
//                                MessageDialog.dialogMessages.removeLast()
//                            }
//                            .build()
//                    )
                }
            }.launchIn(CoroutineScope(IO))
    }

    fun removeFromFoundedClothes(celebImage: CelebImage, vararg fc: FoundedClothes) {
        celebImages.value.forEachIndexed {index, ci ->
            if (ci == celebImage) {
                celebImages.value[index].foundedClothes.removeAll(fc)
                refreshCelebImagesData()
            }
        }
    }



    fun addToFoundedClothes(celebImage: CelebImage, vararg fc: FoundedClothes) {
        celebImages.value.forEachIndexed {index, ci ->
            if (ci == celebImage) {
                celebImages.value[index].foundedClothes.addAll(fc)
                refreshCelebImagesData()
            }
        }
    }

    private fun refreshCelebImagesData() {
        val newCelebImages = ArrayList<CelebImage>()
        newCelebImages.addAll(celebImages.value)
        celebImages.value = arrayListOf()
        celebImages.value = newCelebImages
    }

}

data class CelebImage(
    val image: Bitmap,
    val detectedClothes: ArrayList<DetectedClothes> = arrayListOf(),
    var isProcessed: Boolean = false,
    val foundedClothes: ArrayList<FoundedClothes> = arrayListOf(),
)

data class FoundedClothes(
    val location: RectF,
    val clothes: List<Clothes>
)