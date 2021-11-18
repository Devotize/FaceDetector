package com.sychev.facedetector.presentation.ui.detectorAssitant

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import android.view.View
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes.*
import com.sychev.facedetector.interactors.clothes_list.*
import com.sychev.facedetector.interactors.detected_clothes.InsertDetectedClothes
import com.sychev.facedetector.interactors.gender.DefineGender
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorEvent.*
import com.sychev.facedetector.presentation.ui.assistant_items.SnackbarItem
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
        fun provideGetClothesList(): GetClothes
        fun provideDetectClothesLocal(): DetectClothesLocal
        fun provideDefineGender(): DefineGender
        fun provideInsertDetectedClothes(): InsertDetectedClothes
    }

    private val entryPoint = EntryPointAccessors.fromApplication(context, DetectorViewModelEntryPoint::class.java)
    private val searchClothes = entryPoint.provideSearchClothes()
    private val insertClothesToFavorite = entryPoint.provideInsertClothesToFavorite()
    private val getFavoriteClothes = entryPoint.provideGetFavoriteClothes()
    private val deleteClothes = entryPoint.provideDeleteClothes()
    private val getClothesList = entryPoint.provideGetClothesList()
    private val detectClothesLocal = entryPoint.provideDetectClothesLocal()
    private val defineGender = entryPoint.provideDefineGender()
    private val insertDetectedClothes = entryPoint.provideInsertDetectedClothes()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _clothesList: MutableStateFlow<Pair<View?, FoundedClothesAssistant?>> = MutableStateFlow(Pair(null, null))
    private val _favoriteClothesList: MutableStateFlow<List<Clothes>> = MutableStateFlow(listOf())
    private val _allClothesInCache: MutableStateFlow<List<Clothes>> = MutableStateFlow(listOf())
    private val _selectedButton: MutableStateFlow<SelectedButton?> = MutableStateFlow(null)
    private val _isSelectorMod: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _isActive: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _detectedClothesListLocal: MutableStateFlow<List<DetectedClothes>> = MutableStateFlow(listOf())
    private val _drawMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _insertedRowsLongArray: MutableStateFlow<LongArray> = MutableStateFlow(LongArray(0))
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    val clothesList: StateFlow<Pair<View?, FoundedClothesAssistant?>> = _clothesList.asStateFlow()
    val favoriteClothesList = _favoriteClothesList.asStateFlow()
    val allDetectedClothesInCache = _allClothesInCache.asStateFlow()
    val selectedButton = _selectedButton.asStateFlow()
    val isSelectorMod = _isSelectorMod.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()
    val isActive = _isActive.asStateFlow()
    val detectedClothesListLocal = _detectedClothesListLocal.asStateFlow()
    val drawMode = _drawMode.asStateFlow()
    val insertedRowsLongArray = _insertedRowsLongArray.asStateFlow()

    fun onTriggerEvent(event: DetectorEvent) {
        when (event) {
            is SearchClothesEvent -> {
                searchClothes(detectedClothes = event.detectedClothes, circle = event.circle, context = event.context)
            }
            is InsertClothesToFavoriteEvent -> {
                Log.d(TAG, "onTriggerEvent: InsertClothesToFavoriteEvent")
                insertClothesToFavorite(event.clothes)
            }
            is GetFavoriteClothesEvent -> {
                getFavoriteClothes()
            }
            is DeleteClothesEvent -> {
                Log.d(TAG, "onTriggerEvent: DeleteDetectedCLothesEvent detectedClothes: ${event.clothes}")
                deleteClothes.execute(event.clothes).launchIn(CoroutineScope(IO))
            }
            is GetAllClothes -> {
                getAllClothes()
            }
            is GetNumClothes -> {
                getNumClothes(event.numOfElements)
            }
            is ShareMultiplyUrls -> {
                shareUrls(event.urls)
            }
            is DetectClothesLocalEvent -> {
                Log.d(TAG, "onTriggerEvent: detect clothes event local")
                detectClothesLocal(event.screenshot)
            }
            is DefineGenderEvent -> {
                defineGender(event.screenshot)
            }
            is InsertDetectedClothesEvent -> {
                insertDetectedClothes(event.detectedClothes)
            }
            is ChangeGenderForDetectedClothes -> {
                changeGenderForDetectedClothes(event.location, event.newGender, event.circle, event.context)
            }
        }
    }

    private fun changeGenderForDetectedClothes(location: RectF, newGender: String, circle: View, context: Context) {
        Log.d(TAG, "changeGenderForDetectedClothes: triggered: newGender: $newGender")
        val oldDetectedClothesList = detectedClothesListLocal.value
        val newDetectedClothesList = ArrayList<DetectedClothes>()
        var detectedClothes: DetectedClothes? = null
        oldDetectedClothesList.forEach {
            if (it.location == location) {
                Log.d(TAG, "changeGenderForDetectedClothes: changing gender... ")
                it.gender = newGender
                detectedClothes = it
            }
        }
        newDetectedClothesList.addAll(oldDetectedClothesList)
//        _detectedClothesListLocal.value = ArrayList()
//        _detectedClothesListLocal.value = newDetectedClothesList
        detectedClothes?.let {
            searchClothes(it, context, circle, true)
        }

    }

    private fun searchClothes(detectedClothes: DetectedClothes, context: Context, circle: View, showBigCard: Boolean = false) {
        searchClothes.execute(detectedClothes, context).onEach { dataState ->
            _loading.value = dataState.loading
            dataState.data?.let {
                Log.d(TAG, "searchClothes: detectedClothesList value changed")
                val foundedClothes = FoundedClothesAssistant(detectedClothes.location, it, showBigCard)
                _clothesList.value = Pair(circle, foundedClothes)
            }
            dataState.error?.let{
                circle.isClickable = true
                Log.d(TAG, "searchClothes: error -> ${it}")
                onErrorMessageChange(it)
                CoroutineScope(Main).launch{
                    SnackbarItem(context).open(it)
                }
            }
        }.launchIn(CoroutineScope(IO))
    }

    private fun insertClothesToFavorite(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes).onEach { dataState ->
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

    private fun getNumClothes(numOfElements: Int) {
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

   private fun getAllClothes() {
       // if i ever need to get all clothes in cache
       getClothesList.execute(favoriteOnly = false)
           .onEach { dataState ->
               _loading.value = dataState.loading

               dataState.data?.let {
                    _allClothesInCache.value = it
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

    fun setIsActive(isActive: Boolean) {
        _isActive.value = isActive
    }

    fun setDrawMode(drawMode: Boolean) {
        _drawMode.value = drawMode
    }

    private fun detectClothesLocal(picture: Bitmap) {
        detectClothesLocal.execute(context, picture)
            .onEach { dataState ->
                _loading.value = dataState.loading
                _detectedClothesListLocal.value = ArrayList()
                dataState.data?.let {
                    _detectedClothesListLocal.value = it
                }
            }
            .launchIn(CoroutineScope(IO))
    }

    private fun defineGender(picture: Bitmap) {
        defineGender.execute(context, picture)
            .onEach {dataState ->
                dataState.data?.let{ gender ->
                    Log.d(TAG, "defineGender: $gender")
                    _detectedClothesListLocal.value.forEach {
                        it.gender = gender
                    }
                }
            }.launchIn(CoroutineScope(IO))
    }

    private fun insertDetectedClothes(detectedClothes: List<DetectedClothes>) {
        insertDetectedClothes.execute(detectedClothes = detectedClothes).onEach {
            _loading.value = it.loading
            it.data?.let {
                _insertedRowsLongArray.value = it
            }
        }.launchIn(CoroutineScope(IO))
    }

}

data class FoundedClothesAssistant(
    val location: RectF,
    val clothes: List<Clothes>,
    val showBigCard: Boolean = false,
)















