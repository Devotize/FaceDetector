package com.sychev.facedetector.presentation.ui.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Panorama
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Panorama
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sychev.camera.api.CameraEntryPoint
import com.sychev.common.Destinations
import com.sychev.common.find
import com.sychev.facedetector.presentation.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
    destinations: Destinations,
    launchAssistant: () -> Unit,
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val screens = listOf(
        Screen.FeedList,
        Screen.FavoriteClothesList,
        Screen.Shop,
        Screen.OwnImage,
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        //home screen
        BottomNavigationItem(
            icon = {
                if (Screen.FeedList.route != backStackEntry.value?.destination?.route)
                    Icon(imageVector = Icons.Outlined.Home, contentDescription = null)
                else
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
            },
            selected = Screen.FeedList.route == backStackEntry.value?.destination?.route,
            onClick = {
                navController.navigate(Screen.FeedList.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
        //own image screen
        BottomNavigationItem(
            icon = {
                if (Screen.OwnImage.route != backStackEntry.value?.destination?.route)
                    Icon(
                        imageVector = Icons.Outlined.Panorama,
                        contentDescription = null
                    )
                else
                    Icon(
                        imageVector = Icons.Filled.Panorama,
                        contentDescription = null
                    )
            },
            selected = Screen.OwnImage.route == backStackEntry.value?.destination?.route,
            onClick = {
                    navController.navigate(Screen.OwnImage.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }

            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
        //camera screen
        BottomNavigationItem(
            icon = {
                if (Screen.CameraScreen.route != backStackEntry.value?.destination?.route)
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = null
                    )
                else
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null
                    )
            },
            selected = Screen.CameraScreen.route == backStackEntry.value?.destination?.route,
            onClick = {
//                navController.navigate(Screen.CameraScreen.route) {
//                    popUpTo(navController.graph.findStartDestination().id)
//                    launchSingleTop = true
//                }
                val route = destinations.find<CameraEntryPoint>().destination()
                navController.navigate(route)

            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
    }

}












