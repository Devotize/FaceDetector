package com.sychev.facedetector.presentation.ui.detectorAssitant

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes.DeleteClothes
import com.sychev.facedetector.interactors.clothes.GetClothesList
import com.sychev.facedetector.interactors.clothes.GetFavoriteClothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.utils.TAG
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*

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
    }

    private val entryPoint = EntryPointAccessors.fromApplication(context, DetectorViewModelEntryPoint::class.java)
    private val searchClothes = entryPoint.provideSearchClothes()
    private val insertClothesToFavorite = entryPoint.provideInsertClothesToFavorite()
    private val getFavoriteClothes = entryPoint.provideGetFavoriteClothes()
    private val deleteClothes = entryPoint.provideDeleteClothes()
    private val getClothesList = entryPoint.provideGetClothesList()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _detectedClothesList: MutableStateFlow<List<DetectedClothes>> = MutableStateFlow(listOf())
    private val _favoriteClothesList: MutableStateFlow<List<DetectedClothes>> = MutableStateFlow(listOf())
    private val _lastTenDetectedClothes: MutableStateFlow<List<DetectedClothes>> = MutableStateFlow(listOf())
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    val detectedClothesList: StateFlow<List<DetectedClothes>> = _detectedClothesList.asStateFlow()
    val favoriteClothesList = _favoriteClothesList.asStateFlow()
    val lastTenDetectedClothes = _lastTenDetectedClothes

    fun onTriggerEvent(event: DetectorEvent) {
        when (event) {
            is DetectorEvent.SearchClothesEvent -> {
                searchClothes(bitmap = event.screenshot)
            }
            is DetectorEvent.InsertClothesToFavoriteEvent -> {
                Log.d(TAG, "onTriggerEvent: InsertClothesToFavoriteEvent")
                insertClothesToFavorite(event.detectedClothes)
            }
            is DetectorEvent.GetFavoriteClothesEvent -> {
                getFavoriteClothes()
            }
            is DetectorEvent.DeleteDetectedClothesEvent -> {
                Log.d(TAG, "onTriggerEvent: DeleteDetectedCLothesEvent detectedClothes: ${event.detectedClothes}")
                deleteClothes.execute(event.detectedClothes)
            }
            is DetectorEvent.GetAllDetectedClothes -> {
                getAllDetectedClothes()
            }
            is DetectorEvent.GetNumDetectedClothes -> {
                getNumDetectedClothes(event.numOfElements)
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
            }
        }.launchIn(CoroutineScope(IO))
    }

    private fun insertClothesToFavorite(detectedClothes: DetectedClothes) {
        insertClothesToFavorite.execute(detectedClothes).onEach {
            _loading.value = it.loading
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
        }.launchIn(CoroutineScope(IO))
    }

    private fun getNumDetectedClothes(numOfElements: Int) {
        getClothesList.execute(numOfElements)
            .onEach { dataState ->
                _loading.value = dataState.loading

                dataState.data?.let {
                    _lastTenDetectedClothes.value = it
                }
            }
            .launchIn(CoroutineScope(IO))
    }

   private fun getAllDetectedClothes() {
       // if i ever need to get all clothes in cache
   }
    
}















