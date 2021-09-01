package com.sychev.facedetector.presentation.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationManager {
    val commands = mutableStateOf<Pair<Screen, NavOptionsBuilder.() -> Unit>>(Pair(Screen.Default, {}))

    fun navigate(
        directions: Screen,
        navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
    ){
        commands.value = Pair(Screen.Default, navOptionsBuilder)
        commands.value = Pair(directions, navOptionsBuilder)
    }
}