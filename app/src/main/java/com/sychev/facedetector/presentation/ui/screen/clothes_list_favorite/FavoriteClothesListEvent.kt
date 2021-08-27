package com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite

import com.sychev.facedetector.domain.Clothes

sealed class FavoriteClothesListEvent {
    object GetAllFavoriteClothes: FavoriteClothesListEvent()

    class AddToFavoriteClothesEvent(val clothes: Clothes): FavoriteClothesListEvent()

    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): FavoriteClothesListEvent()

    class NavigateToDetailClothesScreen(val clothes: Clothes): FavoriteClothesListEvent()
}