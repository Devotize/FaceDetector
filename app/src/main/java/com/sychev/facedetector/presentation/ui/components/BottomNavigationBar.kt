package com.sychev.facedetector.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
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
        Screen.Profile,
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
        //fav screen
        BottomNavigationItem(
            icon = {
                if (Screen.FavoriteClothesList.route != backStackEntry.value?.destination?.route)
                    Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null
                    )
                else
                    Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null
                    )
            },
            selected = Screen.FavoriteClothesList.route == backStackEntry.value?.destination?.route,
            onClick = {
                navController.navigate(Screen.FavoriteClothesList.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
        //assistant
        val transition by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2500,
                    easing = LinearEasing,
                    delayMillis = 0
                ),
                repeatMode = RepeatMode.Restart
            )
        )
        Image(
            modifier = Modifier
                .clickable {
                    if (!AssistantDetector.isShown) {
                        launchAssistant()
                    }
                }
                .graphicsLayer {
//                    rotationZ = transition
                },
            painter = painterResource(id = R.drawable.yellow_abstract_icon_final),
            contentDescription = null,
        )
        //shop screen
        BottomNavigationItem(
            icon = {
                if (Screen.Shop.route != backStackEntry.value?.destination?.route)
                    Icon(
                     imageVector = Icons.Outlined.ShoppingCart,
                     contentDescription = null
                    )
                else
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null
                    )
            },
            selected = Screen.Shop.route == backStackEntry.value?.destination?.route,
            onClick = {
                navController.navigate(Screen.Shop.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
        //profile screen
        BottomNavigationItem(
            icon = {
                if (Screen.Profile.route != backStackEntry.value?.destination?.route)
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null
                    )
                else
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null
                    )
            },
            selected = Screen.Profile.route == backStackEntry.value?.destination?.route,
            onClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }

            },
            selectedContentColor = MaterialTheme.colors.secondary,
            unselectedContentColor = MaterialTheme.colors.onBackground
        )
    }

}












