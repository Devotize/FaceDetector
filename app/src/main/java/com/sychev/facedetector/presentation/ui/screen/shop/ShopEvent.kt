package com.sychev.facedetector.presentation.ui.screen.shop

import com.sychev.facedetector.domain.Clothes

sealed class ShopEvent {
    class SearchByFilters(
        val filters: TestClothesFilter
    ): ShopEvent()

    class OnQueryChange(val query: String): ShopEvent()

    object PerformSearchByQuery: ShopEvent()

    class OnGenderChange(val gender: String?): ShopEvent()

    class AddToFavoriteClothesEvent(val clothes: Clothes): ShopEvent()

    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): ShopEvent()

    class GoToFiltersScreen(val customFilter: TestClothesFilter? = null): ShopEvent()

    object GotBackToShopScreen: ShopEvent()

    class GoToDetailClothesScreen(val clothes: Clothes): ShopEvent()

    class ChangeCustomFilters(val newFilters: TestClothesFilter): ShopEvent()

    object SaveCustomClothesFilter: ShopEvent()

    class ReplaceFilterByIndex(val index: Int): ShopEvent()

    object GetTopBrandsEvent: ShopEvent()

}