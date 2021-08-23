package com.sychev.facedetector.presentation.ui.screen.clothes_list_retail

import com.sychev.facedetector.domain.Clothes

sealed class ClothesListRetailEvent {
    class ProcessClothesEvent(val clothes: List<Clothes>, val selectedClothes: List<Clothes>): ClothesListRetailEvent()
    class OnSelectChipEvent(val chip: Pair<Clothes,List<Clothes>>): ClothesListRetailEvent()
    class AddToFavoriteClothesEvent(val clothes: Clothes): ClothesListRetailEvent()
    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): ClothesListRetailEvent()
}