package com.sychev.facedetector.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.*
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.activity.MainActivity
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.components.ClothesRetailItem
import com.sychev.facedetector.presentation.ui.main.MainEvent
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.utils.loadPicture
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@Composable
fun ClothesListRetailScreen(
    clothes: List<DetectedClothes>,
    launcher: MainActivity
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()


    AppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            scaffoldState = scaffoldState,
            drawerContent = {
                Text("Reviews")
            },
            drawerShape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 8.dp,
                bottomEnd = 8.dp,
                bottomStart = 0.dp
            )
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
                ) {
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

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    itemsIndexed(clothes) {index, item ->
                        Card(
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp)
                                .padding(
                                    top = 4.dp,
                                    bottom = 4.dp,
                                    start = if (index == 0) 16.dp else 4.dp,
                                    end = 4.dp
                                ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            loadPicture(url = item.picUrl, defaultImage = R.drawable.clothes_default_icon).value?.let{
                                Image(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }

                val pagerState = rememberPagerState(pageCount = clothes.size, initialOffscreenLimit = 2)

                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    state = pagerState,
                    itemSpacing = 0.dp,
                ) { page ->
                    ClothesRetailItem(
                        modifier = Modifier
                            .width(255.dp)
                            .height(450.dp)
                            .graphicsLayer {
                                val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                                lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }
//                            alpha = lerp(
//                                start = 0.5f,
//                                stop = 1f,
//                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                            )
                            },
                        detectedClothes = clothes[page],
                        onAddToFavoriteClick = {

                        },
                        onRemoveFromFavoriteClick = {

                        }
                    )
                }

//                HorizontalPagerIndicator(
//                    modifier = Modifier
//                        .padding(top = 8.dp)
//                        .align(Alignment.CenterHorizontally),
//                    pagerState = pagerState,
//                    activeColor = MaterialTheme.colors.secondary,
//                    inactiveColor = MaterialTheme.colors.primaryVariant,
//                    indicatorWidth = 4.dp,
//                    indicatorHeight = 4.dp,
//                )

            }
        }

    }
}