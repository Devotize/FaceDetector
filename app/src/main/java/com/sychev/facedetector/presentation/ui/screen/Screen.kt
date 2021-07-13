package com.sychev.facedetector.presentation.ui.screen

sealed class Screen(val route: String) {
    object ClothesList: Screen("clothes_list")
    object FavoriteClothesList: Screen("favorite_clothes_list")
    object Shop: Screen("shop")
    object Profile: Screen("profile")
}
