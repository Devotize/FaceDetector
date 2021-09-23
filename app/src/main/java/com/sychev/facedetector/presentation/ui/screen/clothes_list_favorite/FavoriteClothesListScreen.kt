package com.sychev.facedetector.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.ClothesDetailEvent
import com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite.FavoriteClothesListEvent
import com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite.FavoriteClothesListViewModel
import com.sychev.facedetector.utils.TAG
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun FavoriteClothesListScreen(
    viewModel: FavoriteClothesListViewModel,
) {
    var firstLaunch by remember{ mutableStateOf(true) }
    if (firstLaunch) {
        viewModel.onTriggerEvent(FavoriteClothesListEvent.GetAllFavoriteClothes)
        firstLaunch = false
    }
    val snackbarHostState = remember{SnackbarHostState()}
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    val favoriteClothesList = viewModel.favoriteClothesList
    Scaffold(
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                flingBehavior = StockFlingBehaviours.smoothScroll()
            ) {
                itemsIndexed(favoriteClothesList) { index: Int, item: Clothes ->
                    ClothesItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable {
                                viewModel.onTriggerEvent(
                                    FavoriteClothesListEvent.NavigateToDetailClothesScreen(
                                        item
                                    )
                                )
                            }
                            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
                        clothes = item,
                        onAddToFavoriteClick = {
                            viewModel.onTriggerEvent(
                                FavoriteClothesListEvent.AddToFavoriteClothesEvent(
                                    item
                                )
                            )
                        },
                        onRemoveFromFavoriteClick = {
                            favoriteClothesList.remove(it)
                            CoroutineScope(Main).launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Item removed",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        favoriteClothesList.add(index, it)
                                    }
                                    SnackbarResult.Dismissed -> {
                                        viewModel.onTriggerEvent(
                                            FavoriteClothesListEvent.RemoveFromFavoriteClothesEvent(
                                                item
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

        }
    }


}
