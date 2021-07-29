package com.sychev.facedetector.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.activity.MainActivity
import com.sychev.facedetector.presentation.ui.components.AppTopBar
import com.sychev.facedetector.presentation.ui.components.ClothesBigItem
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.main.MainEvent
import com.sychev.facedetector.presentation.ui.main.MainFragmentViewModel
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.utils.TAG
import java.util.*

@ExperimentalPagerApi
@Composable
fun ClothesListStartScreen(
    viewModel: MainFragmentViewModel,
    launcher: MainActivity
) {

    viewModel.onTriggerEvent(MainEvent.GetAllDetectedClothes)

    val query = viewModel.query.value
    val detectedClothesList = viewModel.detectedClothesList
    val hugeFirstElement = viewModel.hugeFirstElement.value

    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colors.background),
        ) {

        }
    }

}