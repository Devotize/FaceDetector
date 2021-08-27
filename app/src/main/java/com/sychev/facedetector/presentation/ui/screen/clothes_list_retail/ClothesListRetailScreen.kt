package com.sychev.facedetector.presentation.ui.screen.clothes_list_retail

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.*
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.activity.main.MainActivity
import com.sychev.facedetector.presentation.ui.components.ClothesChip
import com.sychev.facedetector.presentation.ui.components.ClothesRetailItem
import com.sychev.facedetector.presentation.ui.components.PagerIndicator
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@Composable
fun ClothesListRetailScreen(
    viewModel: ClothesListRetailViewModel,
    clothesList: List<Clothes>,
    selectedClothes: List<Clothes> = listOf(),
    onBackClick: () -> Unit,
) {
    var recomposed by remember{mutableStateOf(false)}

    if (!recomposed) {
        if (clothesList.isNotEmpty()) {
            viewModel.onTriggerEvent(ClothesListRetailEvent.ProcessClothesEvent(clothesList, selectedClothes))
        }

        if (viewModel.clothesChips.isNotEmpty()) {
            viewModel.onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(viewModel.clothesChips.last()))
        }

        viewModel.clothesChips.forEach { pair ->
            if (pair.second == selectedClothes) {
                viewModel.onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(pair))
            }
        }
        recomposed = true
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = {
                    onBackClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }

        val scrollRowScrollState = rememberLazyListState()
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            state = scrollRowScrollState
        ) {
            itemsIndexed(viewModel.clothesChips) { index, item ->
                ClothesChip(
                    clothes = item.first,
                    isSelected = viewModel.selectedChip.value?.first == item.first,
                    onClick = {
                        viewModel.onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(item))
                    }
                )
            }
        }
        LaunchedEffect(scrollRowScrollState){
            viewModel.selectedChip.value?.let{ pair: Pair<Clothes, List<Clothes>> ->
                scrollRowScrollState.scrollToItem(viewModel.clothesChips.indexOf(pair))
            }
        }

        val pagerState =
            rememberPagerState(pageCount = viewModel.clothesList.size, initialOffscreenLimit = 2)

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
                clothes = viewModel.clothesList[page],
                onAddToFavoriteClick = {
                    viewModel.onTriggerEvent(ClothesListRetailEvent.AddToFavoriteClothesEvent(viewModel.clothesList[page]))
                },
                onRemoveFromFavoriteClick = {
                    viewModel.onTriggerEvent(ClothesListRetailEvent.RemoveFromFavoriteClothesEvent(viewModel.clothesList[page]))
                }
            )
        }

        PagerIndicator(
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
            pagerState = pagerState,
            activeColor = MaterialTheme.colors.secondary,
            inactiveColor = MaterialTheme.colors.primaryVariant,
            activeWidth = 24.dp,
            inactiveWidth = 4.dp,
            inactiveHeight = 4.dp,
            activeHeight = 4.dp,
        )
    }
}



