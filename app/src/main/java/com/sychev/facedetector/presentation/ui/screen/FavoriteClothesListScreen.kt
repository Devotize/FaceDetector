package com.sychev.facedetector.presentation.ui.screen

import android.util.Log
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.sychev.facedetector.presentation.activity.MainActivity
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.main.MainEvent
import com.sychev.facedetector.presentation.ui.main.MainFragmentViewModel
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.utils.TAG
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours
import io.iamjosephmj.flinger.flings.FlingerFlingBehavior
import io.iamjosephmj.flinger.flings.flingBehavior
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun FavoriteClothesListScreen(
    viewModel: MainFragmentViewModel,
    navController: NavController,
) {

    viewModel.onTriggerEvent(MainEvent.GetAllFavoriteClothes)

    val query = viewModel.query.value
    val detectedClothesList = viewModel.savedClothesList
    val hugeFirstElement = viewModel.launchFromAssistant.value
    val nestedNavController = rememberNavController()
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    flingBehavior = StockFlingBehaviours.smoothScroll()
                ) {
                    itemsIndexed(detectedClothesList){index, item ->
                        ClothesItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable {

                                }
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
                            clothes = item,
                            onAddToFavoriteClick = { viewModel.onTriggerEvent(MainEvent.AddToFavoriteClothesEvent(item)) },
                            onRemoveFromFavoriteClick = {viewModel.onTriggerEvent(MainEvent.RemoveFromFavoriteClothesEvent(item))}
                        )
                    }
                }

            }

        }
