package com.sychev.facedetector.presentation.ui.screen.feed_list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
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
import com.sychev.facedetector.utils.MessageDialog
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
@Inject
constructor(
    private val getRandomPics: GetRandomPics,
    private val detectClothesLocal: DetectClothesLocal,
    private val searchClothes: SearchClothes,
    private val getCelebPics: GetCelebPics,
): ViewModel() {
    val urls = mutableStateListOf<String>()
    val pictures = mutableStateListOf<Bitmap>()
    val loading = mutableStateOf(false)
    val processedPages = mutableStateListOf<Int>()
    val detectedClothes = mutableStateListOf<Pair<Int, List<DetectedClothes>>>()
    val foundedClothes = mutableStateListOf<FoundedClothes>()
    var page = 0

    init {
//        val accessKey = "6mDdwXvOc4qruhG8SxW889oVVTKd5VUESYAQrYXKnTE"
//        onTriggerEvent(FeedEvent.GetRandomPicsEvent(
//            accessKey = accessKey,
//            query = "man",
//            count = 25
//        ))
        onTriggerEvent(FeedEvent.GetCelebPicsEvent())
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
                    bitmap = event.bitmap,
                    page = event.page,
                    callback = event.onLoaded
                )
            }
            is FeedEvent.FindClothes -> {
                searchClothes(
                    detectedClothes = event.detectedClothes,
                    context = event.context,
                    page = event.page,
                    location = event.location,
                    callback = event.onLoaded
                )
            }
            is FeedEvent.GetCelebPicsEvent -> {
                getCelebPics.execute(page).onEach { dataState ->
                    loading.value = dataState.loading
                    dataState.data?.let{
                        Log.d(TAG, "onTriggerEvent: getCelebPicsEvent data: $it")
                        pictures.addAll(it)
                        page++
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun detectClothes(context: Context, bitmap: Bitmap, page: Int, callback: (Boolean) -> Unit) {
        detectClothesLocal.execute(context = context, bitmap = bitmap)
            .onEach {dataState ->
                callback(dataState.loading)
                dataState.data?.let{
                    processedPages.add(page)
                    detectedClothes.add(Pair(page, it))
                }
            }.launchIn(viewModelScope)
    }

    private fun searchClothes(detectedClothes: DetectedClothes, context: Context, page: Int, location: RectF, callback: (Boolean) -> Unit) {
        searchClothes.execute(detectedClothes = detectedClothes, context = context)
            .onEach { dataState ->
                callback(dataState.loading)
                dataState.data?.let {
                    val fc = FoundedClothes(
                        page = page,
                        location = location,
                        clothes = it
                    )
                    if (!foundedClothes.contains(fc)){
                        foundedClothes.add(fc)
                        Log.d(TAG, "searchClothes: clothesList = $it")
                    }
                }
                dataState.error?.let { message ->
                    Log.d(TAG, "searchClothes: error: $message")
                    MessageDialog.dialogMessages.add(
                        MessageDialog.Builder()
                            .message(message)
                            .title("Alert")
                            .onDismiss {
                                MessageDialog.dialogMessages.removeLast()
                            }
                            .onPositiveAction {
                                MessageDialog.dialogMessages.removeLast()
                            }
                            .build()
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun removeFromFoundedClothes(fc: FoundedClothes) {
        foundedClothes.remove(fc)
    }

    data class FoundedClothes(
        val page: Int,
        val location: RectF,
        val clothes: List<Clothes>
    )

}