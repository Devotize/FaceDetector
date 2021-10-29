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
            is FeedEvent.FindMultiplyClothes -> {
                searchMultiplyClothes(
                    detectedClothesList = event.detectedClothesList,
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

    private fun searchMultiplyClothes(
        detectedClothesList: List<DetectedClothes>,
        context: Context,
        page: Int,
        location: RectF,
        callback: (Boolean) -> Unit
    ) {
        searchClothes.execute(detectedClothesList = detectedClothesList, context = context)
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
                    Log.d(TAG, "searchMultiplyClothes: foundedClothes: $fc")
                }
//
            }.launchIn(CoroutineScope(IO))
    }

    private fun detectClothes(context: Context, bitmap: Bitmap, page: Int, callback: (Boolean) -> Unit) {
        detectClothesLocal.execute(context = context, bitmap = bitmap)
            .onEach {dataState ->
                callback(dataState.loading)
                dataState.data?.let{
                    processedPages.add(page)
                    detectedClothes.add(Pair(page, it))
                }
            }.launchIn(CoroutineScope(IO))
    }

    private fun searchClothes(detectedClothes: DetectedClothes, context: Context, page: Int, location: RectF, callback: (Boolean?) -> Unit) {
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
                    callback(null)
                    MessageDialog.dialogMessages.add(
                        MessageDialog.Builder()
                            .message("Похожей одежды не найдено")
                            .title("Ошибка")
                            .onDismiss {
                                MessageDialog.dialogMessages.removeLast()
                            }
                            .onPositiveAction {
                                MessageDialog.dialogMessages.removeLast()
                            }
                            .build()
                    )
                }
            }.launchIn(CoroutineScope(IO))
    }

    fun removeFromFoundedClothes(vararg fc: FoundedClothes) {
        foundedClothes.removeAll(fc)
    }

    fun removeFromFoundedClothes(fc: List<FoundedClothes>) {
        foundedClothes.removeAll(fc)
    }

    fun addToFoundedClothes(fc: List<FoundedClothes>) {
        foundedClothes.addAll(fc)
    }



    data class FoundedClothes(
        val page: Int,
        val location: RectF,
        val clothes: List<Clothes>
    )

}