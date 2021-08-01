package com.sychev.facedetector.presentation.ui.screen

sealed class Screen(val route: String) {
    object ClothesListStart: Screen("clothes_list")
    object ClothesListRetail: Screen("clothes_screen_retail")
    object FavoriteClothesList: Screen("favorite_clothes_list")
    object Shop: Screen("shop")
    object Profile: Screen("profile")
}
