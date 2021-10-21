package com.sychev.facedetector.presentation.ui.screen.shop_screen

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastForEachIndexed
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.domain.filter.Price
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.utils.TAG
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours
import kotlin.math.max
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
                            .fillMaxSize()
                            .padding(top = 6.dp),
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp, start = 8.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Сортировка: ",
                                        style = MaterialTheme.typography.h4,
                                        color = MaterialTheme.colors.onBackground
                                    )
                                    Text(
                                        text = "Сначала дешевле",
                                        style = MaterialTheme.typography.h4,
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.clothesList.clear()
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.onPrimary
                                    )
                                }
                            }
                            Spacer(
                                modifier = Modifier
                                    .height(1.dp)
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
                            )
                            LazyColumn(
                                modifier = Modifier,
                                flingBehavior = StockFlingBehaviours.smoothScroll()
                            ) {
                                itemsIndexed(clothesList) { index, item ->
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
                                itemsIndexed(filters) { index: Int, item: TestClothesFilter ->
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
    if (loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colors.secondary
            )
        }
    }

}

@Composable
private fun ShopBrands(
    modifier: Modifier,
    viewModel: ShopViewModel,
    topBrands: List<Brand>,
    brandListScrollState: LazyListState,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.padding(start = 28.dp),
            text = "Бренды",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onPrimary,
        )
        Spacer(
            modifier = Modifier.height(4.dp)
        )

        LazyRow(
            state = brandListScrollState,
        ) {
            itemsIndexed(topBrands) { index: Int, item: Brand ->
                if (index == 0) {
                    Spacer(modifier = Modifier.width(28.dp))
                }
                item.image?.let {
                    Column(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clickable {
                                val newFilter = TestClothesFilter().apply {
                                    brands.add(item.name)
                                }
                                viewModel.onTriggerEvent(
                                    ShopEvent.SearchByFilters(
                                        newFilter
                                    )
                                )
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(62.dp),
                            shape = CircleShape,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colors.primaryVariant
                            )
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                bitmap = item.image.asImageBitmap(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            modifier = Modifier
                                .width(62.dp),
                            text = item.name,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

            }
        }
//        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun ShopSearchBar(
    modifier: Modifier,
    query: String,
    viewModel: ShopViewModel,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        Surface(elevation = 4.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary),
            ) {
                val focusManager = LocalFocusManager.current
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp)
                        .align(Alignment.CenterHorizontally),
                    value = query,
                    textStyle = MaterialTheme.typography.subtitle1,
                    onValueChange = { newValue ->
                        viewModel.onTriggerEvent(ShopEvent.OnQueryChange(query = newValue))
                    },
                    singleLine = true,
                    decorationBox = {
                        Row(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colors.background,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(42.dp)
                                    .padding(start = 6.dp),
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 8.dp, end = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box() {
                                    //hint
                                    if (query.isEmpty())
                                        Text(
                                            text = "Товар, бренд или артикул",
                                            color = MaterialTheme.colors.primaryVariant,
                                            style = MaterialTheme.typography.subtitle1
                                        )
                                    it()
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            viewModel.onTriggerEvent(
                                ShopEvent.SearchByFilters(
                                    TestClothesFilter().apply {
                                        fullTextQuery = query
                                    })
                            )
                        }
                    )
                )
                var selectedTabIndex by remember { mutableStateOf(2) }
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding()
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
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            viewModel.onTriggerEvent(ShopEvent.OnGenderChange(FilterValues.Constants.Gender.male))
                        },
                    ) {
                        Text(
                            text = "Мужчинам",
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.onPrimary

                        )
                    }
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            viewModel.onTriggerEvent(ShopEvent.OnGenderChange(FilterValues.Constants.Gender.female))

                        },
                    ) {
                        Text(
                            text = "Женщинам",
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = {
                            selectedTabIndex = 2
                            viewModel.onTriggerEvent(ShopEvent.OnGenderChange(null))
                        },
                    ) {
                        Text(
                            text = "Все вместе",
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBubble(
    modifier: Modifier = Modifier,
    text: String,
    onCloseClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(1.dp, Color(0xFFF2F2F2)),
        color = MaterialTheme.colors.background,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.onPrimary,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clickable {
                        onCloseClick()
                    },
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }
    }
}

@Composable
private fun ShopFilterBubbles(
    viewModel: ShopViewModel,
    selectedFilter: TestClothesFilter,
    filters: List<TestClothesFilter>,
    selectedFilterIndex: Int?,
    queryBubbles: List<String>
) {
    var isGridExpanded by remember { mutableStateOf(false) }
    Column() {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.clickable {
                    viewModel.onTriggerEvent(
                        ShopEvent.GoToFiltersScreen(
                            selectedFilter
                        )
                    )
                },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Выбрать",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.onPrimary
                )
            }
            if (!filters.contains(selectedFilter)) {
                Row(
                    modifier = Modifier.clickable {
                        selectedFilterIndex?.let {
                            viewModel.filters[it] = selectedFilter
                            viewModel.onTriggerEvent(
                                ShopEvent.SearchByFilters(
                                    filters = selectedFilter
                                )
                            )
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Сохранить",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }

        ExpandableStaggeredHorizontalGrid(
            modifier = Modifier.padding(4.dp),
            isExpanded = isGridExpanded,
            expandButton = {
                Button(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .size(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        contentColor = MaterialTheme.colors.primary
                    ),
                    onClick = {
                        isGridExpanded = !isGridExpanded
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isGridExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
            }
        ) {
            selectedFilter.genders.forEach {
                FilterBubble(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .wrapContentSize(),
                    text = it,
                    onCloseClick = {
                        selectedFilter.genders.remove(it)
                        viewModel.onTriggerEvent(ShopEvent.SearchByFilters(filters = selectedFilter))
                    }
                )
            }

            selectedFilter.colors.forEach {
                FilterBubble(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .wrapContentSize(),
                    text = it,
                    onCloseClick = {
                        selectedFilter.colors.remove(it)
                        viewModel.onTriggerEvent(ShopEvent.SearchByFilters(filters = selectedFilter))
                    }
                )
            }
            selectedFilter.brands.forEach {
                FilterBubble(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .wrapContentSize(),
                    text = it,
                    onCloseClick = {
                        selectedFilter.brands.remove(it)
                        viewModel.onTriggerEvent(ShopEvent.SearchByFilters(filters = selectedFilter))
                    }
                )
            }
            selectedFilter.price.min.let { min ->
                if (min != viewModel.filterValues.price.min) {
                    FilterBubble(
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                            .wrapContentSize(),
                        text = "Одежда от $min ₽",
                        onCloseClick = {
                            selectedFilter.price = Price(
                                min = viewModel.filterValues.price.min,
                                max = selectedFilter.price.max
                            )
                            viewModel.onTriggerEvent(
                                ShopEvent.SearchByFilters(
                                    filters = selectedFilter
                                )
                            )
                        }
                    )
                }
            }
            selectedFilter.price.max?.let { max ->
                if (max != viewModel.filterValues.price.max) {
                    FilterBubble(
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                            .wrapContentSize(),
                        text = "Одежда до $max ₽",
                        onCloseClick = {
                            selectedFilter.price = Price(
                                min = selectedFilter.price.min,
                                max = viewModel.filterValues.price.max,
                            )
                            viewModel.onTriggerEvent(
                                ShopEvent.SearchByFilters(
                                    filters = selectedFilter
                                )
                            )
                        }
                    )
                }
            }
            queryBubbles.forEach { str ->
                FilterBubble(
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                        .wrapContentSize(),
                    text = str,
                    onCloseClick = {
                        var newQuery = ""
                        queryBubbles.forEach {
                            if (it == str) return@forEach
                            newQuery += "$it "
                        }
                        selectedFilter.fullTextQuery = newQuery
                        viewModel.onTriggerEvent(ShopEvent.SearchByFilters(filters = selectedFilter))
                    }
                )
            }
        }
    }
}

@Composable
private fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 } // Keep track of the width of each row
        val rowHeights = IntArray(rows) { 0 } // Keep track of the height of each row

        // Don't constrain child views further, measure them with given constraints
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
            ?: constraints.minWidth
        // Grid's height is the sum of each row
        val height = rowHeights.sum().coerceIn(constraints.minHeight, constraints.maxHeight)

        // y co-ord of each row
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }
        layout(width, height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.place(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
private fun StaggeredHorizontalGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constrains ->
        val screenMaxWidth = constrains.maxWidth
        val screenMaxHeight = constrains.maxHeight
        val rowPlaceablesArrayList: ArrayList<ArrayList<Placeable>> = ArrayList()
        val coordinatesArrayList: ArrayList<ArrayList<Pair<Int, Int>>> = ArrayList()
        rowPlaceablesArrayList.add(arrayListOf())
        coordinatesArrayList.add(arrayListOf())
        var rowIndex = 0
        var width = 0
        var height = 0
        var x = 0
        var y = 0
        measurables.forEach {
            val placeable = it.measure(constrains)
            width += placeable.width
            if (width >= screenMaxWidth) {
                rowIndex++
                width = 0
                rowPlaceablesArrayList.add(arrayListOf())
                coordinatesArrayList.add(arrayListOf())
                x = 0
                y += placeable.height
            }
            height = placeable.height * (rowIndex + 1)

            rowPlaceablesArrayList[rowIndex].add(placeable)
            coordinatesArrayList[rowIndex].add(Pair(x, y))
            x += placeable.width
        }
        layout(width, height) {
            for (i in 0..rowIndex) {
                rowPlaceablesArrayList[i].forEachIndexed { index, placeable ->
                    placeable.place(
                        x = coordinatesArrayList[i][index].first,
                        y = coordinatesArrayList[i][index].second
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandableStaggeredHorizontalGrid(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    expandButton: @Composable () -> Unit = {
        Icon(imageVector = Icons.Default.Expand, contentDescription = null)
    },
    content: @Composable () -> Unit,
) {
    var isMoreThenOneRow = false
    Layout(
        modifier = modifier,
        content = {
            content()
            expandButton()
        }
    ) { measurables, constrains ->
        if (measurables.size == 1) {
            return@Layout layout(0, 0) {

            }
        }
        val screenMaxWidth = constrains.maxWidth
        val screenMaxHeight = constrains.maxHeight
        val rowPlaceablesArrayList: ArrayList<ArrayList<Placeable>> = ArrayList()
        val coordinatesArrayList: ArrayList<ArrayList<Pair<Int, Int>>> = ArrayList()
        rowPlaceablesArrayList.add(arrayListOf())
        coordinatesArrayList.add(arrayListOf())
        var rowIndex = 0
        var width = 0
        var height = 0
        var x = 0
        var y = 0
        run lit@{
            measurables.forEach {
                try {
                    val placeable = it.measure(constrains)
                    width += placeable.width
                    if (width >= screenMaxWidth - 200) {
                        Log.d(TAG, "ExpandableStaggeredHorizontalGrid: moreThenOneRow")
                        isMoreThenOneRow = true
                        if (!isExpanded) {
                            rowPlaceablesArrayList[rowIndex].add(
                                measurables.last().measure(constrains)
                            )
                            coordinatesArrayList[rowIndex].add(Pair(x, y))
                            return@lit
                        }
                        rowIndex++
                        width = 0
                        rowPlaceablesArrayList.add(arrayListOf())
                        coordinatesArrayList.add(arrayListOf())
                        x = 0
                        y += placeable.height
                    }
                    height = placeable.height * (rowIndex + 1)

                    rowPlaceablesArrayList[rowIndex].add(placeable)
                    coordinatesArrayList[rowIndex].add(Pair(x, y))
                    x += placeable.width
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        layout(width, height) {
            for (i in 0..rowIndex) {
                rowPlaceablesArrayList[i].forEachIndexed { index, placeable ->
//                    Log.d(TAG, "ExpandableStaggeredHorizontalGrid: placing placeable with rowIndex: $i")
                    //check if need to add expand button
                    if (i == rowIndex && index == rowPlaceablesArrayList[i].lastIndex && !isMoreThenOneRow) {
                        Log.d(TAG, "ExpandableStaggeredHorizontalGrid: do not add expand button")
                        //do not add expand button
                        return@forEachIndexed
                    } else {
                        placeable.place(
                            x = coordinatesArrayList[i][index].first,
                            y = coordinatesArrayList[i][index].second
                        )
                    }

                }
            }
        }
    }
}



















