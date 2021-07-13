package com.sychev.facedetector.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.MainActivity
import com.sychev.facedetector.presentation.ui.components.AppTopBar
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.main.MainEvent
import com.sychev.facedetector.presentation.ui.main.MainFragmentViewModel
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import java.util.*

@Composable
fun ClothesListScreen(
    viewModel: MainFragmentViewModel,
    launcher: MainActivity
) {

    viewModel.onTriggerEvent(MainEvent.GetAllDetectedClothes)

    val query = viewModel.query.value
    val detectedClothesList = viewModel.detectedClothesList

    AppTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                query = query,
                onQueryChange = viewModel::onQueryChange,
                onStartAssistant = {
                    viewModel.onTriggerEvent(MainEvent.LaunchDetector(launcher))
                }
            )
            LazyColumn(
                modifier = Modifier,
            ) {
                itemsIndexed(detectedClothesList.filter {
                    it.brand.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
                }) { index: Int, item: DetectedClothes ->
                    ClothesItem(
                        detectedClothes = item,
                        onAddToFavoriteClick = {
                            viewModel.onTriggerEvent(
                                MainEvent.AddToFavoriteDetectedClothesEvent(
                                    it
                                )
                            )
                        },
                        onRemoveFromFavoriteClick = {
                            viewModel.onTriggerEvent(
                                MainEvent.RemoveFromFavoriteDetectedClothesEvent(
                                    it
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}