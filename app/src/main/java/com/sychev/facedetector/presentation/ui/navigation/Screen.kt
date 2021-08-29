package com.sychev.facedetector.presentation.ui.navigation

import android.os.Parcelable
import androidx.navigation.compose.NamedNavArgument

sealed class Screen(val route: String, var arguments: Parcelable?) {
    object Default: Screen(route = "default_screen", arguments = null)
    object ClothesListRetail: Screen(route = "clothes_screen_retail", arguments = null)
    object FavoriteClothesList: Screen(route = "favorite_clothes_list", arguments = null)
    object Shop: Screen(route = "shop", arguments = null)
    object Profile: Screen(route = "profile", arguments = null)
    object FeedList: Screen(route = "feed_list", arguments = null)
    object ClothesDetail: Screen(route = "clothes_detail", arguments = null)
}


