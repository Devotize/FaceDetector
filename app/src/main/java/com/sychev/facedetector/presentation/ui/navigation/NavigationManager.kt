package com.sychev.facedetector.presentation.ui.navigation

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationManager {
    val commands = mutableStateOf<Screen>(Screen.Default)

    fun navigate(
        directions: Screen
    ){
        commands.value = Screen.Default
        commands.value = directions
    }
}