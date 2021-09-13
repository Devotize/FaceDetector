package com.sychev.facedetector.presentation.ui.screen.shop_screen

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel
) {
    val query = viewModel.query.value
    val gender = viewModel.gender.value
    val clothesList = viewModel.clothesList
    val filters = viewModel.filters
    val loading = viewModel.loading.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
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
                                            text = "Product, brand or vendor code",
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
                            viewModel.onTriggerEvent(ShopEvent.PerformSearchByQuery)
                        }
                    )
                )
                var selectedTabIndex by remember{ mutableStateOf(2)}
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
                            viewModel.onTriggerEvent(ShopEvent.OnGenderChange(ClothesFilters.Gender.MALE))
                        },
                    ) {
                        Text(
                            text = "Men",
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.onPrimary

                        )
                    }
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                                viewModel.onTriggerEvent(ShopEvent.OnGenderChange(ClothesFilters.Gender.FEMALE))

                        },
                    ) {
                        Text(
                            text = "Women",
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
                            text = "Together",
                            style = MaterialTheme.typography.h3,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
        if (clothesList.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 2.dp, start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sorting: ",
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            text = "Price low first",
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
                                    viewModel.onTriggerEvent(ShopEvent.GoToDetailClothesScreen(item))
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
        } else {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier
                .scrollable(state = scrollState, orientation = Orientation.Vertical),) {

                Row(
                    modifier = Modifier
                        .padding(top = 28.dp, start = 28.dp, end = 28.dp, bottom = 0.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Compilations",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    IconButton(
                        onClick = {
                            viewModel.onTriggerEvent(ShopEvent.GoToFiltersScreen)
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
                    cells = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp),
                ) {
                    itemsIndexed(filters) { index: Int, item: ClothesFilters ->
                        Column(modifier = Modifier.wrapContentSize()) {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .padding(start = 12.dp, end = 12.dp, top = 4.dp)
                                    .size((LocalConfiguration.current.screenWidthDp / 2 - 28).dp)
                                    .clickable {
                                        viewModel.onTriggerEvent(ShopEvent.SearchByFilters(filters = item))
                                    }
                            ) {
                                if (item.clothes?.size == 1) {
                                    item.clothes?.get(0)?.let{ clothes ->
                                        val painter = rememberImagePainter(data = clothes.picUrl){
                                            crossfade(true)
                                            error(R.drawable.clothes_default_icon_gray)
                                        }
                                        Image(
                                            modifier = Modifier.fillMaxSize(),
                                            painter = painter,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop
                                        )

                                    }
                                }else {
                                    item.clothes?.let {
                                        val cellWidth = this.maxHeight.div(2)
                                        LazyVerticalGrid(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            cells = GridCells.Fixed(2)
                                        ) {
                                            itemsIndexed(it) {index: Int, item: Clothes ->
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
                                                    val painter = rememberImagePainter(data = item.picUrl){
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
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
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

    if (loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colors.secondary
            )
        }
    }

}