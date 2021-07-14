package com.sychev.facedetector.presentation.ui.components

import android.util.Log
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sychev.facedetector.presentation.ui.screen.Screen
import com.sychev.facedetector.utils.TAG

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val screens = listOf(
        Screen.ClothesList,
        Screen.FavoriteClothesList,
        Screen.Shop,
        Screen.Profile
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    when (screen.route) {
                        Screen.ClothesList.route -> Icon(imageVector = Icons.Outlined.Home, contentDescription = null)
                        Screen.FavoriteClothesList.route -> Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = null)
                        Screen.Shop.route -> Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = null)
                        Screen.Profile.route -> Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = null)
                    }

                },
                selected = screen.route == backStackEntry.value?.destination?.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                selectedContentColor = MaterialTheme.colors.secondary,
                unselectedContentColor = MaterialTheme.colors.onBackground
            )
        }
    }

}












