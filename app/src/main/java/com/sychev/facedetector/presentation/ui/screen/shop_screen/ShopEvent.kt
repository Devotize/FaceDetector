package com.sychev.facedetector.presentation.ui.screen.shop_screen

import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite.ClothesFilters

sealed class ShopEvent {
    class SearchByFilters(
        val filters: ClothesFilters
    ): ShopEvent()

    class OnQueryChange(val query: String): ShopEvent()

    object PerformSearchByQuery: ShopEvent()

    class OnGenderChange(val gender: ClothesFilters.Gender?): ShopEvent()

    class AddToFavoriteClothesEvent(val clothes: Clothes): ShopEvent()

    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): ShopEvent()

    object GoToFiltersScreen: ShopEvent()

}