package com.sychev.facedetector.presentation.ui.screen.shop_screen.filters_screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ClothesFilters
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ShopViewModel
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
    Box(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
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
                checked = customFilter.gender.contains(ClothesFilters.Gender.FEMALE),
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            it.gender.add(ClothesFilters.Gender.FEMALE)
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                it.gender.remove(ClothesFilters.Gender.FEMALE)
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
                checked = customFilter.gender.contains(ClothesFilters.Gender.MALE),
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            it.gender.add(ClothesFilters.Gender.MALE)
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                it.gender.remove(ClothesFilters.Gender.MALE)
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
                checked = customFilter.novice.contains(ClothesFilters.Novice.NEW),
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            it.novice.add(ClothesFilters.Novice.NEW)
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                it.novice.remove(ClothesFilters.Novice.NEW)
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
                checked = customFilter.popularFlags.contains(ClothesFilters.PopularFlags.POPULAR),
                onCheckedChange = { checked ->
                    if (checked) {
                        val newFilters = customFilter.also {
                            it.popularFlags.add(ClothesFilters.PopularFlags.POPULAR)
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilters))
                    } else {
                        try {
                            val newFilters = customFilter.also {
                                it.popularFlags.remove(ClothesFilters.PopularFlags.POPULAR)
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
                        itemsIndexed(ClothesFilters.ItemCategories.values()) { index: Int, category ->
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
                                    backgroundColor = if (customFilter.itemCategories.contains(
                                            category
                                        )
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
                                    text = category.title,
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
                        itemsIndexed(ClothesFilters.ClothesColors.values()) { index: Int, category ->
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(end = 6.dp),
                                onClick = {
                                    if (customFilter.colors.contains(category)) {
                                        try {
                                            val newFilters = customFilter.also {
                                                it.colors.remove(category)
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
                                            it.colors.add(category)
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
                                    backgroundColor = if (customFilter.colors.contains(category)) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        MaterialTheme.colors.primary
                                    },
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = category.title,
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
                        itemsIndexed(ClothesFilters.Brands.values()) { index: Int, category ->
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(end = 6.dp),
                                onClick = {
                                    if (customFilter.brands.contains(category)) {
                                        try {
                                            val newFilters = customFilter.also {
                                                it.brands.remove(category)
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
                                            it.brands.add(category)
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
                                    backgroundColor = if (customFilter.brands.contains(category)) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        MaterialTheme.colors.primary
                                    },
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = category.title,
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        //min value
                        OutlinedTextField(
                            value = if (customFilter.prices.isNotEmpty()) {
                                customFilter.prices[0].toString()
                            } else {
                                   ""
                                   },
                            onValueChange = {

                            }
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
                        itemsIndexed(ClothesFilters.ItemSizes.values()) { index: Int, category ->
                            OutlinedButton(
                                modifier = Modifier
                                    .padding(end = 6.dp),
                                onClick = {
                                    if (customFilter.itemSizes.contains(category)) {
                                        try {
                                            val newFilters = customFilter.also {
                                                it.itemSizes.remove(category)
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
                                            it.itemSizes.add(category)
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
                                    backgroundColor = if (customFilter.itemSizes.contains(category)) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        MaterialTheme.colors.primary
                                    },
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = category.size.toString(),
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
                        val emptyFilters = ClothesFilters()
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
                        if (!isTitleEmpty) {
                            viewModel.onTriggerEvent(ShopEvent.SaveCustomClothesFilter)
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




















