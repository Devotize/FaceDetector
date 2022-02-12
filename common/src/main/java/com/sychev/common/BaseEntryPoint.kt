package com.sychev.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable

interface EntryPoint {
    val entryRoute: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val deepLinks: List<NavDeepLink>
        get() = emptyList()
}

abstract class BaseEntryPoint: EntryPoint {

    fun NavGraphBuilder.composable(navController: NavHostController, destinations: Destinations, onEnterScreen: () -> Unit = {}) {
        composable(entryRoute, arguments, deepLinks) { backStackEntry ->
            onEnterScreen.invoke()
            onInit(LocalContext.current)
            Composable(navController, destinations, backStackEntry)
        }
    }

    @Composable
    abstract fun NavGraphBuilder.Composable(
        navController: NavHostController,
        destinations: Destinations,
        backStackEntry: NavBackStackEntry,
    )

    open fun onInit(context: Context) {}

}