package com.sychev.facedetector.presentation.ui.screen.clothes_detail

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ClothesDetailViewModel
@Inject constructor(
    private val searchClothes: SearchClothes,
): ViewModel(){
    val similarClothes = mutableStateListOf<Clothes>()
    val loadingSimilarClothes = mutableStateOf(false)

    fun onTriggerEvent(event: ClothesDetailEvent) {
        when (event){
            is ClothesDetailEvent.SearchSimilarClothesEvent -> {
                searchClothesByQuery(event.query, event.size)
            }
        }
    }

    private fun searchClothesByQuery(query: String, size: Int) {
        searchClothes.execute(query, size)
            .onEach { dataState ->
            loadingSimilarClothes.value = dataState.loading
                dataState.data?.let {
                    Log.d(TAG, "searchClothesByQuery: similarClothes: $it")
                    similarClothes.addAll(it)
                }

            }.launchIn(viewModelScope)
    }

}






