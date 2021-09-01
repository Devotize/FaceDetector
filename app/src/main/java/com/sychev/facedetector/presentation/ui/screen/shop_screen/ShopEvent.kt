package com.sychev.facedetector.presentation.ui.screen.shop_screen

import com.sychev.facedetector.domain.Clothes

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

    object GotBackToShopScreen: ShopEvent()

    class ChangeCustomFilters(val newFilters: ClothesFilters): ShopEvent()

    object SaveCustomClothesFilter: ShopEvent()

}