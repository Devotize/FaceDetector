package com.sychev.facedetector.presentation.ui.screen.clothes_list_retail

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.interactors.clothes_list.ProcessClothesForRetail
import com.sychev.facedetector.presentation.activity.main.MainActivity
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ClothesListRetailViewModel
@Inject
constructor(
    private val insertClothesToFavorite: InsertClothesToFavorite,
    private val removeFromFavoriteClothes: RemoveFromFavoriteClothes,
    private val processClothesForRetail: ProcessClothesForRetail,
    private val navigationManager: NavigationManager,
): ViewModel() {

    val clothesList = mutableStateListOf<Clothes>()
    val clothesChips = mutableStateListOf<Pair<Clothes, List<Clothes>>>()
    val selectedChip = mutableStateOf<Pair<Clothes, List<Clothes>>?>(null)

    fun onTriggerEvent(event: ClothesListRetailEvent) {
        when (event) {
            is ClothesListRetailEvent.ProcessClothesEvent -> {
                processClothesForRetail.execute(event.clothes).onEach { dataState ->
                    dataState.data?.let {
                        it.forEachIndexed { index: Int, clothes: Clothes ->
                             if (clothes.provider == "wildberries") {
                                clothesChips.add(Pair(clothes, listOf(clothes, it[index+1])))
                            }
                        }
                        if (clothesChips.isNotEmpty()) {
                            onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(clothesChips.first()))
                        }
                    }
                }.launchIn(viewModelScope)
//                CoroutineScope(IO).launch {
//                    updateChips()
//                }
            }
            is ClothesListRetailEvent.OnSelectChipEvent -> {
                onSelectedChipChanged(event.chip)
            }
            is ClothesListRetailEvent.AddToFavoriteClothesEvent -> {
                addToFavorite(event.clothes)
            }
            is ClothesListRetailEvent.RemoveFromFavoriteClothesEvent -> {
                removeFromFavorite(event.clothes)
            }
            is ClothesListRetailEvent.GoToDetailScreen -> {
                val detailScreen = Screen.ClothesDetail.apply {
                    arguments = arrayListOf(event.clothes)
                }
                navigationManager.navigate(detailScreen)
            }
        }
    }

    private fun onSelectedChipChanged(newSelectedChip: Pair<Clothes, List<Clothes>>) {
        selectedChip.value = newSelectedChip
        clothesList.clear()
        clothesList.addAll(newSelectedChip.second)
    }

    private fun addToFavorite(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes)
            .onEach {
                it.data?.let {
//                    updateChips()
                }
                Log.d(TAG, "addToFavorite: dataState: $it")
            }.launchIn(viewModelScope)
    }

    private fun removeFromFavorite(clothes: Clothes) {
        removeFromFavoriteClothes.execute(clothes)
            .onEach {
                Log.d(TAG, "removeFromFavorite: dataState: $it")
            }.launchIn(viewModelScope)
    }

}