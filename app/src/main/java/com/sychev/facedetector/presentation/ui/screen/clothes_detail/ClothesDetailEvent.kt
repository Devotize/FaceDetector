package com.sychev.facedetector.presentation.ui.screen.clothes_detail

import android.content.Context
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite.FavoriteClothesListEvent

sealed class ClothesDetailEvent {
    class SearchSimilarClothesEvent(val clothes: Clothes, val context: Context): ClothesDetailEvent()
    class GoToDetailScreen(val clothes: Clothes): ClothesDetailEvent()
    class GetClothesFromCache(val clothes: Clothes): ClothesDetailEvent()
    class AddToFavoriteClothesEvent(val clothes: Clothes): ClothesDetailEvent()
    class RemoveFromFavoriteClothesEvent(val clothes: Clothes): ClothesDetailEvent()
    class ShareClothesEvent(val context: Context, val clothes: Clothes): ClothesDetailEvent()

}