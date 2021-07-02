package com.sychev.facedetector.presentation.ui.detectorAssitant

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes.DeleteClothes
import com.sychev.facedetector.interactors.clothes.GetClothesList
import com.sychev.facedetector.interactors.clothes.GetFavoriteClothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorEvent.*
import com.sychev.facedetector.presentation.ui.items.SnackbarItem
import com.sychev.facedetector.utils.TAG
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetectorViewModel(
    private val context: Context
) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DetectorViewModelEntryPoint{
        fun provideSearchClothes(): SearchClothes
        fun provideInsertClothesToFavorite(): InsertClothesToFavorite
        fun provideGetFavoriteClothes(): GetFavoriteClothes
        fun provideDeleteClothes(): DeleteClothes
        fun provideGetClothesList(): GetClothesList
        fun provideDetectClothesLocal(): DetectClothesLocal
    }

    private val entryPoint = EntryPointAccessors.fromApplication(context, DetectorViewModelEntryPoint::class.java)
    private val searchClothes = entryPoint.provideSearchClothes()
    private val insertClothesToFavorite = entryPoint.provideInsertClothesToFavorite()
    private val getFavoriteClothes = entryPoint.provideGetFavoriteClothes()
    private val deleteClothes = entryPoint.provideDeleteClothes()
    private val getClothesList = entryPoint.provideGetClothesList()
    private val detectClothesLocal = entryPoint.provideDetectClothesLocal()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _detectedClothesList: MutableStateFlow<List<DetectedClothes>> = MutableStateFlow(listOf())
    private val _favoriteClothesList: MutableStateFlow<List<DetectedClothes>> = MutableStateFlow(listOf())
    private val _allDetectedClothesInCache: MutableStateFlow<List<DetectedClothes>> = MutableStateFlow(listOf())
    private val _selectedButton: MutableStateFlow<SelectedButton?> = MutableStateFlow(null)
    private val _isSelectorMod: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    val detectedClothesList: StateFlow<List<DetectedClothes>> = _detectedClothesList.asStateFlow()
    val favoriteClothesList = _favoriteClothesList.asStateFlow()
    val allDetectedClothesInCache = _allDetectedClothesInCache.asStateFlow()
    val selectedButton = _selectedButton.asStateFlow()
    val isSelectorMod = _isSelectorMod.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()

    fun onTriggerEvent(event: DetectorEvent) {
        when (event) {
            is SearchClothesEvent -> {
                searchClothes(bitmap = event.screenshot)
            }
            is InsertClothesToFavoriteEvent -> {
                Log.d(TAG, "onTriggerEvent: InsertClothesToFavoriteEvent")
                insertClothesToFavorite(event.detectedClothes)
            }
            is GetFavoriteClothesEvent -> {
                getFavoriteClothes()
            }
            is DeleteDetectedClothesEvent -> {
                Log.d(TAG, "onTriggerEvent: DeleteDetectedCLothesEvent detectedClothes: ${event.detectedClothes}")
                deleteClothes.execute(event.detectedClothes).launchIn(CoroutineScope(IO))
            }
            is GetAllDetectedClothes -> {
                getAllDetectedClothes()
            }
            is GetNumDetectedClothes -> {
                getNumDetectedClothes(event.numOfElements)
            }
            is ShareMultiplyUrls -> {
                shareUrls(event.urls)
            }
            is DetectClothesLocalEvent -> {
                detectClothesLocal.execute(context, event.screenshot)
                    .onEach {
                        Log.d(TAG, "onTriggerEvent: DetectClothesLocalEvent ${it.data}")
                    }
                    .launchIn(CoroutineScope(IO))
            }
        }
    }

    private fun searchClothes(bitmap: Bitmap) {
        searchClothes.execute(bitmap, context).onEach { dataState ->
            _loading.value = dataState.loading
            dataState.data?.let {
                Log.d(TAG, "searchClothes: detectedClothesList value changed")
                _detectedClothesList.value = it
            }
            dataState.error?.let{
                Log.d(TAG, "searchClothes: error -> ${it}")
                onErrorMessageChange(it)
                CoroutineScope(Main).launch{
                    SnackbarItem(context).open(it)
                }
            }
        }.launchIn(CoroutineScope(IO))
    }

    private fun insertClothesToFavorite(detectedClothes: DetectedClothes) {
        insertClothesToFavorite.execute(detectedClothes).onEach { dataState ->
            _loading.value = dataState.loading
            dataState.error?.let{
                Log.d(TAG, "searchClothes: error -> ${it}")
                onErrorMessageChange(it)
            }
        }.launchIn(CoroutineScope(IO))
    }

    private fun getFavoriteClothes() {
        getFavoriteClothes.execute().onEach { dataState ->
            Log.d(TAG, "getFavoriteClothes: called")
            _loading.value = dataState.loading
            dataState.data?.let {
                Log.d(TAG, "getFavoriteClothes: data: $it")
                _favoriteClothesList.value = it
            }
            dataState.error?.let{
                Log.d(TAG, "searchClothes: error -> ${it}")
                onErrorMessageChange(it)
            }
        }.launchIn(CoroutineScope(IO))
    }

    private fun getNumDetectedClothes(numOfElements: Int) {
        getClothesList.execute(numOfElements)
            .onEach { dataState ->
                _loading.value = dataState.loading

                dataState.data?.let {
//                    _lastTenDetectedClothes.value = it
                }
                dataState.error?.let{
                    Log.d(TAG, "searchClothes: error -> ${it}")
                    onErrorMessageChange(it)
                }
            }
            .launchIn(CoroutineScope(IO))
    }

   private fun getAllDetectedClothes() {
       // if i ever need to get all clothes in cache
       getClothesList.execute()
           .onEach { dataState ->
               _loading.value = dataState.loading

               dataState.data?.let {
                    _allDetectedClothesInCache.value = it
               }
               dataState.error?.let{
                   Log.d(TAG, "searchClothes: error -> ${it}")
                   onErrorMessageChange(it)
               }
           }
           .launchIn(CoroutineScope(IO))
   }

    fun onSelectedButtonChange(newSelectedButton: SelectedButton?) {
        _selectedButton.value = newSelectedButton
    }

    fun onSelectorModeChanged(newValue: Boolean) {
        _isSelectorMod.value = newValue
    }

    private fun onErrorMessageChange(newMessage: String) {
        _errorMessage.value = newMessage
    }

    private fun shareUrls(urls: ArrayList<String>) {
        if (urls.isEmpty()) return
        var stringToShare = ""
        urls.forEach {
            stringToShare = "$stringToShare \n $it"
        }
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing Url")
        intent.putExtra(Intent.EXTRA_TEXT, stringToShare)
        context.startActivity(Intent.createChooser(intent, "Share Url").apply{flags = Intent.FLAG_ACTIVITY_NEW_TASK})
    }

}















