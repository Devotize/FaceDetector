package com.sychev.facedetector.presentation.ui.screen.shop

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.screen.shop.components.ShopBrands
import com.sychev.facedetector.presentation.ui.screen.shop.components.ShopFilterBubbles
import com.sychev.facedetector.presentation.ui.screen.shop.components.ShopSearchBar
import com.sychev.facedetector.utils.TAG
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel
) {
    val query = viewModel.query.value
    val queryBubbles = viewModel.queryBubbles
    val gender = viewModel.gender.value
    val clothesList = viewModel.clothesList
    val filters = viewModel.filters
    val loading = viewModel.loading.value
    val selectedFilter = viewModel.selectedFilter.value
    var selectedFilterIndex by remember { mutableStateOf<Int?>(null) }
    filters.fastForEachIndexed { i, testClothesFilter ->
        if (testClothesFilter == selectedFilter) {
            selectedFilterIndex = i
        }
    }
    val topBrands = viewModel.topBrands
    val brandListScrollState = rememberLazyListState()
    var showSortDialog by remember { mutableStateOf(false)}

    var selectedSortTabIndex by remember{ mutableStateOf(2)}



    Box(modifier = Modifier.fillMaxSize()) {
        var searchBarHeight by remember{mutableStateOf(0.dp)}
        var searchBarHeightInPx by remember{ mutableStateOf(0f)}.also {
            searchBarHeight = with(LocalDensity.current) {it.value.toDp()}
        }
        var brandsBubblesHeight by remember{mutableStateOf(0.dp)}
        var brandsBubblesHeightInPx by remember{ mutableStateOf(0f)}.also {
            brandsBubblesHeight = with(LocalDensity.current) {it.value.toDp()}
        }
        var brandsBubblesOffset by remember {mutableStateOf(0.dp)}
        var brandsBubblesOffsetPx by remember {mutableStateOf(0f)}.also {
            brandsBubblesOffset = with(LocalDensity.current){it.value.toDp()}
        }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y
                    val newOffset: Float = brandsBubblesOffsetPx + delta
                    brandsBubblesOffsetPx = newOffset.coerceIn(-brandsBubblesHeightInPx, 0f)
                    return Offset.Zero
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.primary),
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = searchBarHeight)
                .nestedScroll(nestedScrollConnection),
            ) {
                if (clothesList.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned {
                                    brandsBubblesHeightInPx = it.size.toSize().height
                                }
                                .offset { IntOffset(x = 0, y = brandsBubblesOffsetPx.roundToInt()) }
                        ) {
                            selectedFilter?.let { selectedFilter ->
                                ShopFilterBubbles(
                                    viewModel = viewModel,
                                    selectedFilter = selectedFilter,
                                    filters = filters,
                                    selectedFilterIndex = selectedFilterIndex,
                                    queryBubbles = queryBubbles
                                )
                            }
                        }
                        var topPadding by remember{mutableStateOf(0.dp)}
                        topPadding = if (brandsBubblesOffset + brandsBubblesHeight < 0.dp) {
                            0.dp
                        } else {
                            brandsBubblesOffset + brandsBubblesHeight
                        }
                        Column(
                            modifier = Modifier
                                .padding(top = topPadding),
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .height(0.5.dp)
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
                            )
                            TabRow(
                                selectedTabIndex = selectedSortTabIndex,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 8.dp)
                                    .wrapContentHeight(),
                                divider = {
                                    TabRowDefaults.Divider(
                                        color = MaterialTheme.colors.primary
                                    )
                                }
                            ) {
                                Tab(
                                    modifier = Modifier
                                        .padding(bottom = 8.dp),
                                    selected = selectedSortTabIndex == 0,
                                    onClick = {
                                        selectedSortTabIndex = 0
                                        viewModel.sortClothesLowerFirst()
                                    },
                                ) {
                                    Text(
                                        text = "Дешевле",
                                        style = MaterialTheme.typography.h5,
                                        color = MaterialTheme.colors.onPrimary

                                    )
                                }
                                Tab(
                                    selected = selectedSortTabIndex == 1,
                                    onClick = {
                                        selectedSortTabIndex = 1
                                        viewModel.sortClothesHigherFirst()
                                    },
                                ) {
                                    Text(
                                        text = "Дороже",
                                        style = MaterialTheme.typography.h5,
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                }
                                Tab(
                                    selected = selectedSortTabIndex == 2,
                                    onClick = {
                                        selectedSortTabIndex = 2
                                        viewModel.sortClothesByDefault()
                                    },
                                ) {
                                    Text(
                                        text = "По умолчанию",
                                        style = MaterialTheme.typography.h5,
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                }
                            }
                            Spacer(
                                modifier = Modifier
                                    .height(0.5.dp)
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
                            )
                            LazyColumn(
                                modifier = Modifier,
                                flingBehavior = StockFlingBehaviours.smoothScroll()
                            ) {
                                itemsIndexed(clothesList) { index, item ->
                                    if (item.itemId == Clothes.NothingFoundClothes.ITEM_ID_NOTHING_FOUND) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = "Ничего не найдено",
                                                color = MaterialTheme.colors.onPrimary,
                                                style = MaterialTheme.typography.h3,
                                            )
                                        }
                                    }else {
                                    ClothesItem(
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.onTriggerEvent(
                                                    ShopEvent.GoToDetailClothesScreen(
                                                        item
                                                    )
                                                )
                                            },
                                        clothes = item,
                                        shape = RectangleShape,
                                        onAddToFavoriteClick = {
                                            viewModel.onTriggerEvent(
                                                ShopEvent.AddToFavoriteClothesEvent(
                                                    item
                                                )
                                            )
                                        },
                                        onRemoveFromFavoriteClick = {
                                            viewModel.onTriggerEvent(
                                                ShopEvent.RemoveFromFavoriteClothesEvent(
                                                    item
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier,
                    ) {
                        //brands
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned {
                                    brandsBubblesHeightInPx = it.size.toSize().height
                                }
                                .offset { IntOffset(x = 0, y = brandsBubblesOffsetPx.roundToInt()) },
                        ) {
                            ShopBrands(
                                modifier = Modifier
                                    .padding(top = 16.dp, bottom = 8.dp)
                                    .fillMaxWidth(),
                                viewModel = viewModel,
                                topBrands = topBrands,
                                brandListScrollState = brandListScrollState,
                            )
                        }
                        var topPadding by remember{mutableStateOf(0.dp)}
                        topPadding = if (brandsBubblesOffset + brandsBubblesHeight < 0.dp) {
                            0.dp
                        } else {
                            brandsBubblesOffset + brandsBubblesHeight
                        }
                        Column(
                            modifier = Modifier
                                .padding(top = topPadding),
                        ){
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp, start = 28.dp, end = 28.dp, bottom = 0.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Подборки",
                                    style = MaterialTheme.typography.subtitle1,
                                    color = MaterialTheme.colors.onPrimary,
                                )
                                IconButton(
                                    onClick = {
                                        viewModel.onTriggerEvent(ShopEvent.GoToFiltersScreen())
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(38.dp),
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }
                            LazyVerticalGrid(
                                cells = GridCells.Adaptive(140.dp),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp,
                                    top = 0.dp
                                ),
                            ) {
                                itemsIndexed(filters) { index: Int, item: ClothesFilter ->
                                    Column(modifier = Modifier.wrapContentSize()) {
                                        BoxWithConstraints(
                                            modifier = Modifier
                                                .padding(start = 12.dp, end = 12.dp, top = 4.dp)
                                                .size((LocalConfiguration.current.screenWidthDp / 2 - 28).dp)
                                                .clickable {
                                                    Log.d(
                                                        TAG,
                                                        "ShopScreen: item.colors: ${item.colors}"
                                                    )
                                                    viewModel.onTriggerEvent(
                                                        ShopEvent.SearchByFilters(
                                                            filters = item
                                                        )
                                                    )
                                                }
                                        ) {
                                            if (item.clothes?.size == 1) {
                                                item.clothes?.get(0)?.let { clothes ->
                                                    val painter = rememberImagePainter(data = clothes.picUrl) {
                                                        crossfade(true)
                                                        error(R.drawable.clothes_default_icon_gray)
                                                    }
                                                    Image(
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                        painter = painter,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                    )

                                                }
                                            } else {
                                                item.clothes?.let {
                                                    val cellWidth = this.maxHeight.div(2)
                                                    LazyVerticalGrid(
                                                        modifier = Modifier
                                                            .fillMaxSize(),
                                                        cells = GridCells.Fixed(2),
                                                    ) {
                                                        itemsIndexed(
                                                            it.subList(
                                                                0,
                                                                4
                                                            )
                                                        ) { index: Int, item: Clothes ->
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(cellWidth)
                                                                    .padding(
                                                                        top = 0.dp,
                                                                        end = if (index % 2 == 0) 2.dp else 0.dp,
                                                                        start = if (index % 2 != 0) 2.dp else 0.dp,
                                                                        bottom = if (index == it.lastIndex || index == it.lastIndex - 1) 0.dp else 4.dp
                                                                    )
                                                                    .background(MaterialTheme.colors.primary),
                                                            ) {
                                                                val painter =
                                                                    rememberImagePainter(data = item.picUrl) {
                                                                        crossfade(true)
                                                                        error(R.drawable.clothes_default_icon_gray)
                                                                    }
                                                                Image(
                                                                    modifier = Modifier
                                                                        .fillMaxSize(),
                                                                    painter = painter,
                                                                    contentDescription = null,
                                                                    contentScale = ContentScale.Crop
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (item.clothes == null || item.clothes?.isEmpty() == true) {
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxSize(),
                                                    color = MaterialTheme.colors.background
                                                ) {

                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            modifier = Modifier.padding(
                                                start = 12.dp,
                                                top = 4.dp,
                                                bottom = 4.dp
                                            ),
                                            text = item.title,
                                            style = MaterialTheme.typography.h6,
                                            color = MaterialTheme.colors.onPrimary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ShopSearchBar(
                modifier = Modifier
                    .wrapContentSize()
                    .onGloballyPositioned {
                        searchBarHeightInPx = it.size.toSize().height
                    },
                query = query,
                viewModel = viewModel,
            )
        }
    }
    if (showSortDialog) {
        Dialog(
            onDismissRequest = {
                showSortDialog = false
            }
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.primary, MaterialTheme.shapes.medium),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                        .clickable {
                            showSortDialog = false
                        },
                    text = "Сначала дешевле",
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h3,
                )
                Spacer(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(180.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colors.background, MaterialTheme.shapes.small),
                )
                Text(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                        .clickable {
                            showSortDialog = false
                        },
                    text = "Сначала дороже",
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h3
                )
            }
        }
    }
    if (loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colors.secondary
            )
        }
    }

}



















