package com.sychev.facedetector.presentation.ui.screen.clothes_list_retail

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.*
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.ui.components.ClothesBigItem
import com.sychev.facedetector.presentation.ui.components.ClothesChip
import com.sychev.facedetector.presentation.ui.components.ShopComponent
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.SimilarClothesCard
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toMoneyString
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun ClothesListRetailScreen(
    viewModel: ClothesListRetailViewModel,
    detectedClothes: MutableList<DetectedClothes>,
    onBackClick: () -> Unit,
) {
    var recomposed by remember{mutableStateOf(false)}
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Log.d(TAG, "ClothesListRetailScreen: detectedClothes: $detectedClothes")
    
    if (!recomposed) {
        if (viewModel.listOfClothesList.isEmpty()) {
            detectedClothes.forEach {
                Log.d(TAG, "ClothesListRetailScreen: finding clothes for detectedCLOTHES")
                viewModel.onTriggerEvent(ClothesListRetailEvent.FindClothes(it, context))
            }
            recomposed = true
        }
    }

    val clothesChips = viewModel.clothesChips
    // looking for wich list of clothes is active
    val bigClothesLazyList = rememberLazyListState()
    val clothesChipsContainsIndexes = viewModel.clothesChipsContainsIndexes

    if (viewModel.listOfClothesList.isEmpty()) {
        LoadingClothesRetailScreen {
            onBackClick()
        }
    } else {
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
                    itemsIndexed(clothesChips) { index, item ->
                        if (clothesChipsContainsIndexes.size > index) {
                            ClothesChip(
                                clothes = item,
                                isSelected = clothesChipsContainsIndexes[index].contains(
                                    bigClothesLazyList.firstVisibleItemIndex
                                ),
                                onClick = {
                                    coroutineScope.launch {
                                        bigClothesLazyList.animateScrollToItem(
                                            clothesChipsContainsIndexes[index].first()
                                        )

                                    }
//                            viewModel.onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(item, context = context))
                                }
                            )
                        }
                    }
                }

            }
            LazyColumn(
                state = bigClothesLazyList,
                flingBehavior = StockFlingBehaviours.smoothScroll(),
            ) {
                Log.d(
                    TAG,
                    "ClothesListRetailScreen: bigClothesLazyList: ${bigClothesLazyList.firstVisibleItemIndex}"
                )
                viewModel.listOfClothesList.forEachIndexed { listIndex, it ->
                    itemsIndexed(it) { index, item ->
//                    Log.d(TAG, "ClothesListRetailScreen: listIndex: $listIndex")
                        Column {
                            ClothesBigItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 2.dp),
                                clothes = item,
                                clothesList = it,
                                onAddToFavoriteClick = {
                                    viewModel.onTriggerEvent(
                                        ClothesListRetailEvent.AddToFavoriteClothesEvent(
                                            clothes = it
                                        )
                                    )
                                },
                                onRemoveFromFavoriteClick = {
                                    viewModel.onTriggerEvent(
                                        ClothesListRetailEvent.RemoveFromFavoriteClothesEvent(
                                            clothes = it
                                        )
                                    )
                                },
                                onShareClick = {
                                    viewModel.onTriggerEvent(
                                        ClothesListRetailEvent.ShareClothesEvent(
                                            item,
                                            context
                                        )
                                    )
                                },
                                onShoppingCartClick = {
                                    detectedClothes.clear()
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.clothesUrl))
                                    ContextCompat.startActivity(context, browserIntent, null)
                                },
                                filterValues = viewModel.filterValues,
                            )
                            if (!viewModel.similarClothes.contains(index)) {
                                viewModel.onTriggerEvent(
                                    ClothesListRetailEvent.GetSimilarClothes(
                                        clothes = item,
                                        context = context,
                                        index = index,
                                    )
                                )
                            }
                            //similar clothes
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = "Похожие товары",
                                style = MaterialTheme.typography.h5,
                                color = MaterialTheme.colors.onPrimary
                            )

                            if (viewModel.similarClothes[index] == null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(
                                    flingBehavior = StockFlingBehaviours.smoothScroll()
                                ) {
                                    repeat(6) {
                                        item {
                                            Spacer(modifier = Modifier.width(12.dp))
                                            LoadingSimilarClothes()
                                        }
                                    }
                                }
                            }

                            viewModel.similarClothes[index]?.let { clothesList ->
                                LazyRow() {
                                    itemsIndexed(clothesList) { index, item ->
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

        }
    }
}

@Composable
fun LoadingClothesRetailScreen(
    onBackClick: () -> Unit
) {
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
                repeat(6) {
                    item {
                        LoadingClothesChip()
                    }
                }
            }
        }
        LazyColumn() {
            repeat(2) {
                item {
                    Column {
                        LoadingBigItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 2.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .height(16.dp)
                                .width(144.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colors.background
                        ) {
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow() {
                            repeat(6) {
                                item {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    LoadingSimilarClothes()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingClothesChip() {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(60.dp)
            .padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 4.dp,
                end = 4.dp
            ),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.background
    ) {
    }
}

@Composable
private fun LoadingBigItem(
    modifier: Modifier,
) {
    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colors.background
            ){
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp),
                    color = MaterialTheme.colors.background
                ){}
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(top = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.background
            ) {}
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(top = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.background
            ) {}

        }
    }
}

@Composable
private fun LoadingSimilarClothes() {
    Column(

    ) {
        Surface(
            modifier = Modifier
                .width(114.dp)
                .height(164.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colors.background
        ){}
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier
                .height(16.dp)
                .width(114.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.background
        ){}
        Spacer(modifier = Modifier.height(4.dp))
    }
}



















