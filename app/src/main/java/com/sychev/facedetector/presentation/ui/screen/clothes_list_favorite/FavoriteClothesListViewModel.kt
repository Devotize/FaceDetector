package com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.interactors.clothes.GetClothes
import com.sychev.facedetector.interactors.clothes.GetFavoriteClothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FavoriteClothesListViewModel
@Inject constructor(
    private val getFavoriteClothes: GetFavoriteClothes,
    private val insertClothesToFavorite: InsertClothesToFavorite,
    private val removeFromFavoriteClothes: RemoveFromFavoriteClothes,
    private val navigationManager: NavigationManager,
    private val getClothes: GetClothes,
): ViewModel(){

    val favoriteClothesList = mutableStateListOf<Clothes>()
    val loading = mutableStateOf(false)

    fun onTriggerEvent(event: FavoriteClothesListEvent) {
        when (event) {
            is FavoriteClothesListEvent.GetAllFavoriteClothes -> {
                getFavoriteClothes()
            }
            is FavoriteClothesListEvent.AddToFavoriteClothesEvent -> {
                addToFavoriteClothes(event.clothes)
            }
            is FavoriteClothesListEvent.RemoveFromFavoriteClothesEvent -> {
                removeFromFavoriteClothes(event.clothes)
            }
            is FavoriteClothesListEvent.NavigateToDetailClothesScreen -> {
                val screen = Screen.ClothesDetail.apply {
                    arguments = arrayListOf(event.clothes, event.clothes)
                }
                    navigationManager.navigate(screen)

            }
        }
    }

    fun getFavoriteClothes() {
        getFavoriteClothes.execute()
            .onEach { dataState ->
                loading.value = dataState.loading
                dataState.data?.let{
                    favoriteClothesList.clear()
                    favoriteClothesList.addAll(it.reversed())
                }
            }.launchIn(viewModelScope)
    }

    private fun removeFromFavoriteClothes(clothes: Clothes) {
        Log.d(TAG, "removeFromFavoriteClothes: called")
        removeFromFavoriteClothes.execute(clothes)
            .onEach { dataState ->
                dataState.data?.let {
                    refreshClothesList()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addToFavoriteClothes(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes)
            .onEach { dataState ->
                dataState.data?.let {
                    refreshClothesList()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshClothesList() {
        getClothes.execute(favoriteClothesList).onEach { dataState ->
            dataState.data?.let{
                favoriteClothesList.clear()
                favoriteClothesList.addAll(it)
            }
        }.launchIn(viewModelScope)
    }

}