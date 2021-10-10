package com.sychev.facedetector.presentation.ui.screen.shop_screen.filters_screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.domain.filter.Price
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ShopViewModel
import com.sychev.facedetector.presentation.ui.screen.shop_screen.TestClothesFilter
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.color

@OptIn(ExperimentalUnitApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
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

    var firsLaunch by remember{ mutableStateOf(true)}

    if (firsLaunch) {
        viewModel.customFilter.value.price = viewModel.filterValues.price
        firsLaunch = false
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
                    text = "Создать свю подборку",
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
                    Text(text = "Название")
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
                title = "Женщинам",
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
                title = "Мужчинам",
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
                title = "Только новинки",
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
                title = "Только популярные",
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
                        text = "Категоря одежды",
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
                        itemsIndexed(viewModel.filterValues.itemCategories.second) { index: Int, category ->
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
                        text = "Цвет",
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
                        modifier = Modifier.padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(viewModel.filterValues.colors.second) { index: Int, ct ->
                            var isAlreadySelected by remember{mutableStateOf(false)}
                            isAlreadySelected = customFilter.colors.contains(ct.colorName)
                            val buttonSize = if (isAlreadySelected) 46.dp else 36.dp
                            val borderStroke = if (isAlreadySelected)
                                BorderStroke(2.dp, MaterialTheme.colors.secondary)
                            else
                                BorderStroke(1.dp, MaterialTheme.colors.onPrimary)
                            OutlinedButton(
                                modifier = Modifier
                                    .size(buttonSize),
                                onClick = {
                                    Log.d(TAG, "FiltersScreen: colors filters: ${isAlreadySelected}")
                                    if (isAlreadySelected) {
                                        try {
                                            val newFilters = customFilter.also {
                                                    it.colors.remove(ct.colorName)
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
                                            it.colors.add(ct.colorName)
                                        }
                                        viewModel.onTriggerEvent(
                                            ShopEvent.ChangeCustomFilters(
                                                newFilters
                                            )
                                        )
                                    }
                                },
                                border = borderStroke,
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = ("#" + ct.colorHex).color,
                                    contentColor = MaterialTheme.colors.onPrimary
                                ),
                                contentPadding = PaddingValues(1.dp)
                            ) {
                                if (isAlreadySelected) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Default.Done,
                                        contentDescription = null,
                                        tint = MaterialTheme.colors.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
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
                        text = "Бренд",
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
                        itemsIndexed(viewModel.filterValues.brands.second) { index: Int, ct ->
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
                        text = "Цена",
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                value = if (customFilter.price.min == viewModel.filterValues.price.min) {
                                    ""
                                } else {
                                    customFilter.price.min.toString()
                                },
                                onValueChange = { str ->
                                    val newFilter = if (str.isNotEmpty()) {
                                        customFilter.also {
                                            it.price = Price(str.toInt(), it.price.max)
                                        }
                                    } else {
                                        customFilter.also {
                                            it.price = Price(viewModel.filterValues.price.min, it.price.max)
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
                                    Text(text = "Мин")
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
                                value = if (customFilter.price.max == viewModel.filterValues.price.max) {
                                    ""
                                } else {
                                    customFilter.price.max.toString()
                                },
                                onValueChange = { str ->
                                    val newFilter = if (str.isNotEmpty()) {
                                        customFilter.also {
                                            it.price = Price(it.price.min, str.toInt())
                                        }
                                    } else {
                                        customFilter.also {
                                            it.price = Price(it.price.min, viewModel.filterValues.price.max)
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
                                    Text(text = "Макс")
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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        RangeSlider(
                            modifier = Modifier
                                .fillMaxWidth(),
                            values = customFilter.price.min.toFloat()..customFilter.price.max.toFloat(),
                            onValueChange = { floatRange ->
                                Log.d(TAG, "FiltersScreen: onValueCange floatRange = ${floatRange.endInclusive.toInt()}")
                                val newFilter = customFilter.also {
                                    it.price = Price(floatRange.start.toInt(), floatRange.endInclusive.toInt())
                                }
                                viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilter))
                            },
                            valueRange = viewModel.filterValues.price.min.toFloat()..viewModel.filterValues.price.max.toFloat(),
                            colors = SliderDefaults.colors(
                                activeTrackColor = MaterialTheme.colors.secondary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
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
                        text = "Размер",
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
                        val emptyFilters = TestClothesFilter().apply {
                            price = viewModel.filterValues.price
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(emptyFilters))
                    },
                    shape = CircleShape,
                ) {
                    Text(
                        text = "Сбросить",
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
                        isMinPriceLowerThenMax = customFilter.price.min < customFilter.price.max
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
                        text = "Сохранить",
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




















