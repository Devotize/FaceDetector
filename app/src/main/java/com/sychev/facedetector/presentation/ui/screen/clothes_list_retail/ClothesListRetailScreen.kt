package com.sychev.facedetector.presentation.ui.screen.clothes_list_retail

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.content.ContextCompat
import com.google.accompanist.pager.*
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.activity.main.MainActivity
import com.sychev.facedetector.presentation.ui.components.ClothesBigItem
import com.sychev.facedetector.presentation.ui.components.ClothesChip
import com.sychev.facedetector.presentation.ui.components.ClothesRetailItem
import com.sychev.facedetector.presentation.ui.components.PagerIndicator
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.ClothesDetailEvent
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.SimilarClothesCard
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
    val context = LocalContext.current

    if (!recomposed) {
        if (clothesList.isNotEmpty() && viewModel.clothesList.isEmpty()) {
            viewModel.onTriggerEvent(ClothesListRetailEvent.ProcessClothesEvent(clothesList, context))
        }

        if (viewModel.clothesChips.isNotEmpty()) {
            viewModel.onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(viewModel.clothesChips.last(), context = context))
        }

        viewModel.clothesChips.forEach { pair ->
            if (pair.second == selectedClothes) {
                viewModel.onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(pair, context))
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
        ) {
            IconButton(
                modifier = Modifier
                    .padding(top = 18.dp, start = 4.dp)
                    .size(30.dp),
                onClick = {
                    onBackClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            val scrollRowScrollState = rememberLazyListState()
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                state = scrollRowScrollState
            ) {
                itemsIndexed(viewModel.clothesChips) { index, item ->
                    ClothesChip(
                        clothes = item.first,
                        isSelected = viewModel.selectedChip.value?.first == item.first,
                        onClick = {
                            viewModel.onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(item, context = context))
                        }
                    )
                }
            }
            LaunchedEffect(scrollRowScrollState) {
                viewModel.selectedChip.value?.let { pair: Pair<Clothes, List<Clothes>> ->
                    scrollRowScrollState.scrollToItem(viewModel.clothesChips.indexOf(pair))
                }
            }

        }
        LazyColumn() {
            itemsIndexed(viewModel.clothesList) {index, item ->
                ClothesBigItem(
                    clothes = item,
                    onAddToFavoriteClick = {
                        viewModel.onTriggerEvent(ClothesListRetailEvent.AddToFavoriteClothesEvent(clothes = it))
                                           },
                    onRemoveFromFavoriteClick = {
                        viewModel.onTriggerEvent(ClothesListRetailEvent.RemoveFromFavoriteClothesEvent(clothes = it))
                    },
                    onShareClick = {
                        viewModel.onTriggerEvent(ClothesListRetailEvent.ShareClothesEvent(item, context))
                    },
                    onShoppingCartClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.clothesUrl))
                        ContextCompat.startActivity(context, browserIntent, null)
                    }
                )
                if (!viewModel.similarClothes.contains(index)) {
                    viewModel.onTriggerEvent(ClothesListRetailEvent.GetSimilarClothes(clothes = item, context= context, index = index,))
                }
                viewModel.similarClothes[index]?.let { clothesList ->
                    LazyRow() {
                        itemsIndexed(clothesList){index, item ->
                            Spacer(modifier = Modifier.width(12.dp))
                            SimilarClothesCard(
                            modifier = Modifier
                                .clickable {
                                    viewModel.onTriggerEvent(
                                        ClothesListRetailEvent.GoToDetailScreen(
                                            clothes = item
                                        )
                                    )
                                },
                            clothes = item,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))


            }

        }

    }
}



















