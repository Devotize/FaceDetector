package com.sychev.facedetector.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.sychev.facedetector.presentation.activity.MainActivity
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.main.MainEvent
import com.sychev.facedetector.presentation.ui.main.MainFragmentViewModel
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun ClothesListStartScreen(
    viewModel: MainFragmentViewModel,
    launcher: MainActivity
) {

    viewModel.onTriggerEvent(MainEvent.GetAllDetectedClothes)

    val query = viewModel.query.value
    val detectedClothesList = viewModel.detectedClothesList
    val hugeFirstElement = viewModel.launchFromAssistant.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .width(73.dp)
                        .height(73.dp),
                    onClick = {
                        viewModel.onTriggerEvent(MainEvent.LaunchDetector(launcher))
                    },
                    shape = CircleShape,
                    backgroundColor = MaterialTheme.colors.secondary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 6.dp)
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = null,
                    )
                }
            },
            drawerContent = {
                Text("Reviews")
            },
            drawerShape = RoundedCornerShape(topStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp, bottomStart = 0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(start = 18.dp, top = 18.dp, end = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    IconButton(onClick = {
                        if (scaffoldState.drawerState.isClosed) {
                            scope.launch { scaffoldState.drawerState.open() }
                        } else {
                            scope.launch { scaffoldState.drawerState.close() }
                        }

                    }) {
                        Icon(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = null
                        )
                    }
                    Text(
                        modifier = Modifier
                            .clickable {
                                       launcher.finish()
                            },
                        text = "Close",
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.h3,
                    )
                }

                LazyColumn() {
                    itemsIndexed(detectedClothesList){index, item ->
                        ClothesItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
                            detectedClothes = item,
                            onAddToFavoriteClick = { viewModel.onTriggerEvent(MainEvent.AddToFavoriteDetectedClothesEvent(item)) },
                            onRemoveFromFavoriteClick = {viewModel.onTriggerEvent(MainEvent.RemoveFromFavoriteDetectedClothesEvent(item))}
                        )
                    }
                }

            }

        }
    }

}