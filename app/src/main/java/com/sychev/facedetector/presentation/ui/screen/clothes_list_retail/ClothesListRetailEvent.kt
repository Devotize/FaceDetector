package com.sychev.facedetector.presentation.ui.screen.clothes_list_retail

import android.content.Context
import com.sychev.facedetector.domain.Clothes

sealed class ClothesListRetailEvent {
    class ProcessClothesEvent(val clothes: List<Clothes>, val context: Context): ClothesListRetailEvent()
    class OnSelectChipEvent(val chip: Pair<Clothes,List<Clothes>>, val context: Context): ClothesListRetailEvent()
    class AddToFavoriteClothesEvent(val clothes: Clothes): ClothesListRetailEvent()
    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): ClothesListRetailEvent()
    class GoToDetailScreen( val clothes: Clothes): ClothesListRetailEvent()
    class GetSimilarClothes(val clothes: Clothes, val context: Context, val index: Int): ClothesListRetailEvent()
    class ShareClothesEvent(val clothes: Clothes, val context: Context): ClothesListRetailEvent()
}