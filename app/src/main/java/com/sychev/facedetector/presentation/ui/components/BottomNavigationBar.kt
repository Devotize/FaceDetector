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
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sychev.camera.api.CameraEntryPoint
import com.sychev.common.Destinations
import com.sychev.common.find
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.feature.gallery.api.GalleryEntryPoint

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
        val galleryRoute = remember { destinations.find<GalleryEntryPoint>().entryRoute }
        BottomNavigationItem(
            icon = {
                if (galleryRoute != backStackEntry.value?.destination?.route)
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
            selected = galleryRoute == backStackEntry.value?.destination?.route,
            onClick = {
                navController.navigate(galleryRoute)
            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
        //camera screen
        val cameraRoute = remember { destinations.find<CameraEntryPoint>().entryRoute }
        BottomNavigationItem(
            icon = {
                if (cameraRoute != backStackEntry.value?.destination?.route)
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
            selected = cameraRoute == backStackEntry.value?.destination?.route,
            onClick = {
                navController.navigate(cameraRoute)

            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
    }

}












