package com.sychev.facedetector.presentation.ui.screen.shop.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.domain.filter.Price
import com.sychev.facedetector.presentation.ui.screen.shop.ClothesFilter
import com.sychev.facedetector.presentation.ui.screen.shop.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop.ShopViewModel

@Composable
fun ShopFilterBubbles(
    viewModel: ShopViewModel,
    selectedFilter: ClothesFilter,
    filters: List<ClothesFilter>,
    selectedFilterIndex: Int?,
    queryBubbles: List<String>
) {
    var isGridExpanded by remember { mutableStateOf(false) }
    var isTitleAlreadyExsists by remember { mutableStateOf(false) }
    viewModel.filters.forEach {
        if (it.title == selectedFilter.title) {
            isTitleAlreadyExsists = true
        }
    }
    Column(
    ) {
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
            if (isTitleAlreadyExsists && !filters.contains(selectedFilter)) {
                Row(
                    modifier = Modifier.clickable {
                        selectedFilterIndex?.let {
                            viewModel.filters[it] = selectedFilter
//                            viewModel.onTriggerEvent(
//                                ShopEvent.SearchByFilters(
//                                    filters = selectedFilter
//                                )
//                            )
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
                        .size(24.dp),
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
            //gender
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
            //color
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
            //brand
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
            //from price
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
            //to price
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
            // popular only
            selectedFilter.popular.let { popular ->
                if (popular == FilterValues.Constants.Popular.popular) {
                    FilterBubble(
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                            .wrapContentSize(),
                        text = "Популярное",
                        onCloseClick = {
                            selectedFilter.popular = FilterValues.Constants.Popular.default
                            viewModel.onTriggerEvent(
                                ShopEvent.SearchByFilters(
                                    filters = selectedFilter
                                )
                            )
                        }
                    )
                }
            }
            // novice only
            selectedFilter.novice.let { novice ->
                if (novice == FilterValues.Constants.Novice.new) {
                    FilterBubble(
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                            .wrapContentSize(),
                        text = "Новинки",
                        onCloseClick = {
                            selectedFilter.novice = FilterValues.Constants.Novice.default
                            viewModel.onTriggerEvent(
                                ShopEvent.SearchByFilters(
                                    filters = selectedFilter
                                )
                            )
                        }
                    )
                }
            }
            // category
            selectedFilter.itemCategories.forEach {
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
                        selectedFilter.itemCategories.remove(it)
                        viewModel.onTriggerEvent(ShopEvent.SearchByFilters(filters = selectedFilter))
                    }
                )
            }
            //query
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
                .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onPrimary,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clickable {
                        onCloseClick()
                    },
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }
        }
    }
}