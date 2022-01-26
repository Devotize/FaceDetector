package com.sychev.facedetector.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sychev.facedetector.R
import com.sychev.facedetector.presentation.ui.detectorAssitant.AssistantDetector
import com.sychev.facedetector.presentation.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
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
                navController.navigate(Screen.CameraScreen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }

            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
    }

}












