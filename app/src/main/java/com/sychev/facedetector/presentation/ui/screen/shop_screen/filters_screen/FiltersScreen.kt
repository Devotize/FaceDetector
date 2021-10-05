package com.sychev.facedetector.presentation.ui.screen.shop_screen.filters_screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ClothesFilters
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ShopViewModel
import com.sychev.facedetector.presentation.ui.screen.shop_screen.TestClothesFilter
import com.sychev.facedetector.utils.TAG
import kotlin.concurrent.timerTask

@OptIn(ExperimentalUnitApi::class)
@Composable
fun FiltersScreen(
    viewModel: ShopViewModel
) {
    val customFilter = viewModel.customFilter.value
    val scrollState = rememberScrollState()
    var isTitleEmpty by remember { mutableStateOf(false) }
    var isMinPriceLowerThenMax by remember { mutableStateOf(true)}
    var filterIndex by remember{ mutableStateOf<Int?>(null)}
    viewModel.filters.forEachIndexed() { index, it ->
        if (it == customFilter) {
            Log.d(TAG, "FiltersScreen: customFilterAlreadyExists")
            filterIndex = index
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colors.primary)
                .fillMaxSize(),
        ) {
            val focusManager = LocalFocusManager.current
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
                    viewModel.onTriggerEvent(ShopEvent.GotBackToShopScreen)
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIos,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Create own compilation",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onPrimary
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = customFilter.title,
                isError = isTitleEmpty,
                onValueChange = { str ->
                    isTitleEmpty = str.isEmpty()
                    val newFilters = customFilter.also {
                        it.title = str
                    }
                    viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                },
                label = {
                    Text(text = "Title")
                },
                singleLine = true,
                shape = CircleShape,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = MaterialTheme.colors.onPrimary,
                    textColor = MaterialTheme.colors.onPrimary,
                    focusedBorderColor = MaterialTheme.colors.secondary,
                    focusedLabelColor = MaterialTheme.colors.secondary,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
            RowWithCheckBox(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                title = "Women",
                checked = customFilter.genders.contains(FilterValues.Constants.Gender.female),
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            it.genders.add(FilterValues.Constants.Gender.female)
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                it.genders.remove(FilterValues.Constants.Gender.female)
                            }
                            viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
            RowWithCheckBox(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                title = "Men",
                checked = customFilter.genders.contains(FilterValues.Constants.Gender.male),
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            it.genders.add(FilterValues.Constants.Gender.male)
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                it.genders.remove(FilterValues.Constants.Gender.male)
                            }
                            viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
            )
            RowWithCheckBox(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                title = "Only new",
                checked = customFilter.novice == FilterValues.Constants.Novice.new,
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            it.novice = FilterValues.Constants.Novice.new
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                it.novice = FilterValues.Constants.Novice.default
                            }
                            viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
            )
            RowWithCheckBox(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                title = "Only popular",
                checked = customFilter.popular == FilterValues.Constants.Popular.popular,
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            customFilter.popular = FilterValues.Constants.Popular.popular
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                customFilter.popular = FilterValues.Constants.Popular.default
                            }
                            viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
            )
            // itemCategories
            Column(
                modifier = Modifier
                    .animateContentSize(spring(1.75f))
                    .fillMaxWidth()
            ) {
                var showItemsRow by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Clothes Category",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    IconButton(
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }
                if (showItemsRow) {
                    LazyRow(
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        itemsIndexed(viewModel.filterValues.itemCategories) { index: Int, category ->
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(end = 6.dp),
                                onClick = {
                                    if (customFilter.itemCategories.contains(category)) {
                                        try {
                                            val newFilters = customFilter.also {
                                                it.itemCategories.remove(category)
                                            }
                                            viewModel.onTriggerEvent(
                                                ShopEvent.ChangeCustomFilters(
                                                    newFilters
                                                )
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        val newFilters = customFilter.also {
                                            it.itemCategories.add(category)
                                        }
                                        viewModel.onTriggerEvent(
                                            ShopEvent.ChangeCustomFilters(
                                                newFilters
                                            )
                                        )
                                    }
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = if (customFilter.itemCategories.contains(category)
                                    ) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        MaterialTheme.colors.primary
                                    },
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = category,
                                    style = MaterialTheme.typography.h6,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
            )
            //colors
            Column(
                modifier = Modifier
                    .animateContentSize(spring(1.75f))
                    .fillMaxWidth()
            ) {
                var showItemsRow by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Colors",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    IconButton(
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }
                if (showItemsRow) {
                    LazyRow(
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        itemsIndexed(viewModel.filterValues.colors) { index: Int, ct ->
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(end = 6.dp),
                                onClick = {
                                    if (customFilter.colors.contains(ct)) {
                                        try {
                                            val newFilters = customFilter.also {
                                                it.colors.remove(ct)
                                            }
                                            viewModel.onTriggerEvent(
                                                ShopEvent.ChangeCustomFilters(
                                                    newFilters
                                                )
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        val newFilters = customFilter.also {
                                            it.colors.add(ct)
                                        }
                                        viewModel.onTriggerEvent(
                                            ShopEvent.ChangeCustomFilters(
                                                newFilters
                                            )
                                        )
                                    }
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = if (customFilter.colors.contains(ct)) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        MaterialTheme.colors.primary
                                    },
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = ct,
                                    style = MaterialTheme.typography.h6,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
            )
            //brand
            Column(
                modifier = Modifier
                    .animateContentSize(spring(1.75f))
                    .fillMaxWidth()
            ) {
                var showItemsRow by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Brand",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    IconButton(
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }
                if (showItemsRow) {
                    LazyRow(
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        itemsIndexed(viewModel.filterValues.brands) { index: Int, ct ->
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(end = 6.dp),
                                onClick = {
                                    if (customFilter.brands.contains(ct)) {
                                        try {
                                            val newFilters = customFilter.also {
                                                it.brands.remove(ct)
                                            }
                                            viewModel.onTriggerEvent(
                                                ShopEvent.ChangeCustomFilters(
                                                    newFilters
                                                )
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        val newFilters = customFilter.also {
                                            it.brands.add(ct)
                                        }
                                        viewModel.onTriggerEvent(
                                            ShopEvent.ChangeCustomFilters(
                                                newFilters
                                            )
                                        )
                                    }
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = if (customFilter.brands.contains(ct)) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        MaterialTheme.colors.primary
                                    },
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = ct,
                                    style = MaterialTheme.typography.h6,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
            )
            //price
            Column(
                modifier = Modifier
                    .animateContentSize(spring(1.75f))
                    .fillMaxWidth()
            ) {
                var showItemsRow by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Price",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    IconButton(
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }
                if (showItemsRow) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //min value
                        OutlinedTextField(
                            modifier = Modifier.width(130.dp),
                            singleLine = true,
                            isError = !isMinPriceLowerThenMax,
                            value = if (customFilter.price.first == 0) {
                                ""
                            } else {
                                customFilter.price.first.toString()
                                   },
                            onValueChange = { str ->
                                val newFilter = if (str.isNotEmpty()) {
                                    customFilter.also {
                                        it.price = Pair(str.toInt(), it.price.second)
                                    }
                                } else {
                                    customFilter.also {
                                        it.price = Pair(0, it.price.second)
                                    }
                                }
                                viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilter))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            label = {
                                Text(text = "Min price")
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                cursorColor = MaterialTheme.colors.onPrimary,
                                textColor = MaterialTheme.colors.onPrimary,
                                focusedBorderColor = MaterialTheme.colors.secondary,
                                focusedLabelColor = MaterialTheme.colors.secondary,
                            )
                        )
                        //max value
                        OutlinedTextField(
                            modifier = Modifier.width(130.dp),
                            singleLine = true,
                            isError = !isMinPriceLowerThenMax,
                            value = if (customFilter.price.second == 1000000000) {
                                ""
                            } else {
                                customFilter.price.second.toString()
                            },
                            onValueChange = { str ->
                                val newFilter = if (str.isNotEmpty()) {
                                    customFilter.also {
                                        it.price = Pair(it.price.first, str.toInt())
                                    }
                                } else {
                                    customFilter.also {
                                        it.price = Pair(it.price.first, 1000000000)
                                    }
                                }
                                viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilter))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            label = {
                                Text(text = "Max price")
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                cursorColor = MaterialTheme.colors.onPrimary,
                                textColor = MaterialTheme.colors.onPrimary,
                                focusedBorderColor = MaterialTheme.colors.secondary,
                                focusedLabelColor = MaterialTheme.colors.secondary,
                            )
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
            )
            //size
            Column(
                modifier = Modifier
                    .animateContentSize(spring(1.75f))
                    .fillMaxWidth()
            ) {
                var showItemsRow by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Size",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    IconButton(
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }
                if (showItemsRow) {
                    LazyRow(
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        itemsIndexed(viewModel.filterValues.itemSizes) { index: Int, ct ->
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(end = 6.dp),
                                onClick = {
                                    if (customFilter.itemSizes.contains(ct)) {
                                        try {
                                            val newFilters = customFilter.also {
                                                it.itemSizes.remove(ct)
                                            }
                                            viewModel.onTriggerEvent(
                                                ShopEvent.ChangeCustomFilters(
                                                    newFilters
                                                )
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        val newFilters = customFilter.also {
                                            it.itemSizes.add(ct)
                                        }
                                        viewModel.onTriggerEvent(
                                            ShopEvent.ChangeCustomFilters(
                                                newFilters
                                            )
                                        )
                                    }
                                },
                                border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = if (customFilter.itemSizes.contains(ct)) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        MaterialTheme.colors.primary
                                    },
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = ct,
                                    style = MaterialTheme.typography.h6,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primaryVariant, CircleShape)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {

                OutlinedButton(
                    border = BorderStroke(width = 1.dp, MaterialTheme.colors.onPrimary),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colors.primary
                    ),
                    onClick = {
                        val emptyFilters = TestClothesFilter()
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(emptyFilters))
                    },
                    shape = CircleShape,
                ) {
                    Text(
                        text = "Reset",
                        fontSize = TextUnit(value = 24f, TextUnitType.Sp),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colors.secondary
                    ),
                    onClick = {
                        isTitleEmpty = customFilter.title.isEmpty()
                        isMinPriceLowerThenMax = customFilter.price.first < customFilter.price.second
                        if (!isTitleEmpty && isMinPriceLowerThenMax) {
                            if (filterIndex != null) {
                                filterIndex?.let {
                                    viewModel.onTriggerEvent(ShopEvent.ReplaceFilterByIndex(it))
                                }
                            } else {
                                viewModel.onTriggerEvent(ShopEvent.SaveCustomClothesFilter)
                            }
                        }
                    },
                    shape = CircleShape,
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Save",
                        fontSize = TextUnit(value = 24f, TextUnitType.Sp),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                }

            }
        }
    }
}

@Composable
private fun RowWithCheckBox(
    title: String,
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onPrimary,
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}




















