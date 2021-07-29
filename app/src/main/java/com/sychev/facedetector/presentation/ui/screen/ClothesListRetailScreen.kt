package com.sychev.facedetector.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.theme.AppTheme

@ExperimentalPagerApi
@Composable
fun ClothesListRetailScreen(
    clothes: List<DetectedClothes>
) {

    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val pagerState = rememberPagerState(pageCount = clothes.size, initialOffscreenLimit = 2)
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState
            ) { page ->
                ClothesItem(
                    detectedClothes = clothes[page],
                    onAddToFavoriteClick = {

                    },
                    onRemoveFromFavoriteClick = {

                    })

            }
        }
    }

}