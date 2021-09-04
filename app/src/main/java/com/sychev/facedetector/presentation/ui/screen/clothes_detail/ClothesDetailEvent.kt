package com.sychev.facedetector.presentation.ui.screen.clothes_detail

import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite.FavoriteClothesListEvent

sealed class ClothesDetailEvent {
    class SearchSimilarClothesEvent(val query: String, val size: Int): ClothesDetailEvent()
    class GoToDetailScreen(val clothes: Clothes): ClothesDetailEvent()
    class GetClothesFromCache(val clothes: Clothes): ClothesDetailEvent()
    class AddToFavoriteClothesEvent(val clothes: Clothes): ClothesDetailEvent()
    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): ClothesDetailEvent()

}