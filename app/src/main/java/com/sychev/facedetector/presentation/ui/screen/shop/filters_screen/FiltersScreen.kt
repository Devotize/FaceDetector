package com.sychev.facedetector.presentation.ui.screen.shop.filters_screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.flowlayout.FlowColumn
import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.domain.filter.Price
import com.sychev.facedetector.presentation.ui.screen.shop.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop.ShopViewModel
import com.sychev.facedetector.presentation.ui.screen.shop.ClothesFilter
import com.sychev.facedetector.presentation.ui.screen.shop.components.ExpandButton
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.color
import kotlinx.coroutines.launch

@OptIn(ExperimentalUnitApi::class, androidx.compose.material.ExperimentalMaterialApi::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
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
            Row(modifier = Modifier.fillMaxWidth()) {
                RowWithCheckBox(
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp, end = 18.dp)
                        .fillMaxWidth(.5f),
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
                Spacer(modifier = Modifier.width(24.dp))
                RowWithCheckBox(
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp)
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
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                RowWithCheckBox(
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp, end = 18.dp)
                        .fillMaxWidth(.5f),
                    title = "Новинки",
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
                Spacer(modifier = Modifier.width(24.dp))
                RowWithCheckBox(
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp)
                        .fillMaxWidth(),
                    title = "Популярные",
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
            }
            // itemCategories
            Column(
                modifier = Modifier
                    .animateContentSize(spring(1.75f))
                    .fillMaxWidth()
            ) {
                var showItemsRow by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Категоря одежды",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onPrimary,
                    )
                    ExpandButton(
                        isExpanded = showItemsRow,
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    )

                }
                val categoryScrollState = rememberScrollState()
                var heightOfItemInDp by remember{ mutableStateOf(0.dp)}
                var heightOfItemInPx by remember{mutableStateOf(0)}.also {
                    heightOfItemInDp = with(LocalDensity.current) {it.value.toDp()}
                }
                val verticalSpaceBetweenItemsInDp = 2.dp
                if (showItemsRow) {
                    FlowColumn(
                        modifier = Modifier
                            .horizontalScroll(categoryScrollState, true)
                            .height((heightOfItemInDp + verticalSpaceBetweenItemsInDp) * 3)
                    ) {
                        viewModel.filterValues.itemCategories.second.forEach { category ->
                            OutlinedButton(
                                modifier = Modifier
                                    .onGloballyPositioned {
                                        heightOfItemInPx = it.size.height
                                    }
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
                                    text = category,
                                    style = MaterialTheme.typography.h6,
                                )
                            }
                            Spacer(modifier = Modifier.height(verticalSpaceBetweenItemsInDp))
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
                    ExpandButton(
                        isExpanded = showItemsRow,
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    )
                }
                val colorScrollState = rememberScrollState()
                val boxHeightInDp = 37.dp
                if (showItemsRow) {
                    FlowColumn(
                        modifier = Modifier
                            .horizontalScroll(colorScrollState, true)
                            .height((boxHeightInDp) * 2),
                    ) {
                        viewModel.filterValues.colors.second.forEach { ct ->
                            var isAlreadySelected by remember{mutableStateOf(false)}
                            isAlreadySelected = customFilter.colors.contains(ct.colorName)
                            val buttonSize = if (isAlreadySelected) 33.dp else 26.dp
                            val borderStroke = if (isAlreadySelected)
                                BorderStroke(2.dp, MaterialTheme.colors.secondary)
                            else
                                BorderStroke(1.dp, MaterialTheme.colors.onPrimary)
                            Box(modifier = Modifier
                                .size(boxHeightInDp)) {
                                OutlinedButton(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(buttonSize),
                                    onClick = {
                                        Log.d(
                                            TAG,
                                            "FiltersScreen: colors filters: ${isAlreadySelected}"
                                        )
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
                            }
                        }
                    }
                }
//
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
                    ExpandButton(
                        isExpanded = showItemsRow,
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    )
                }
                val brandScrollState = rememberScrollState()
                var heightOfItemInDp by remember{ mutableStateOf(0.dp)}
                var heightOfItemInPx by remember{mutableStateOf(0)}.also {
                    heightOfItemInDp = with(LocalDensity.current) {it.value.toDp()}
                }
                val verticalSpaceBetweenItemsInDp = 8.dp
                if (showItemsRow) {
                            FlowColumn(
                                modifier = Modifier
                                    .horizontalScroll(brandScrollState)
                                    .height((heightOfItemInDp + verticalSpaceBetweenItemsInDp) * 3),
                            ) {
                                viewModel.topBrands.forEachIndexed { index, item ->
                                    val ct = item.name
                                    item.image?.let { image ->
                                        Row(
                                            modifier = Modifier,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                modifier = Modifier
                                                    .width(104.dp)
                                                    .height(68.dp)
                                                    .onGloballyPositioned {
                                                        heightOfItemInPx = it.size.height
                                                    },
                                                bitmap = image.asImageBitmap(),
                                                contentDescription = null,
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Checkbox(
                                                checked = customFilter.brands.contains(ct),
                                                onCheckedChange = {
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
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                }
//                if (showItemsRow) {
//                    LazyRow(
//                        modifier = Modifier.padding(bottom = 6.dp)
//                    ) {
//                        itemsIndexed(viewModel.filterValues.brands.second) { index: Int, ct ->
//                            OutlinedButton(
//                                modifier = Modifier
//                                    .padding(end = 6.dp),
//                                onClick = {
//                                    if (customFilter.brands.contains(ct)) {
//                                        try {
//                                            val newFilters = customFilter.also {
//                                                it.brands.remove(ct)
//                                            }
//                                            viewModel.onTriggerEvent(
//                                                ShopEvent.ChangeCustomFilters(
//                                                    newFilters
//                                                )
//                                            )
//                                        } catch (e: Exception) {
//                                            e.printStackTrace()
//                                        }
//                                    } else {
//                                        val newFilters = customFilter.also {
//                                            it.brands.add(ct)
//                                        }
//                                        viewModel.onTriggerEvent(
//                                            ShopEvent.ChangeCustomFilters(
//                                                newFilters
//                                            )
//                                        )
//                                    }
//                                },
//                                border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
//                                shape = CircleShape,
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    backgroundColor = if (customFilter.brands.contains(ct)) {
//                                        MaterialTheme.colors.secondary
//                                    } else {
//                                        MaterialTheme.colors.primary
//                                    },
//                                    contentColor = MaterialTheme.colors.onPrimary
//                                )
//                            ) {
//                                Text(
//                                    modifier = Modifier,
//                                    text = ct,
//                                    style = MaterialTheme.typography.h6,
//                                )
//                            }
//                        }
//                    }
//                }
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
                    ExpandButton(
                        isExpanded = showItemsRow,
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    )
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
                        if (customFilter.price.max != null && viewModel.filterValues.price.max != null) {
                            RangeSlider(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                values = customFilter.price.min.toFloat()..customFilter.price.max!!.toFloat(),
                                onValueChange = { floatRange ->
                                    Log.d(TAG, "FiltersScreen: onValueCange floatRange = ${floatRange.endInclusive.toInt()}")
                                    val newFilter = customFilter.also {
                                        it.price = Price(floatRange.start.toInt(), floatRange.endInclusive.toInt())
                                    }
                                    viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(newFilter))
                                },
                                valueRange = viewModel.filterValues.price.min.toFloat()..viewModel.filterValues.price.max!!.toFloat(),
                                colors = SliderDefaults.colors(
                                    activeTrackColor = MaterialTheme.colors.secondary
                                )
                            )
                        }
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
                    ExpandButton(
                        isExpanded = showItemsRow,
                        onClick = {
                            showItemsRow = !showItemsRow
                        }
                    )
                }
                Log.d(TAG, "FiltersScreen: filtersizes: ${viewModel.filterValues.itemSizes}")
                if (showItemsRow) {
                    LazyRow(
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        itemsIndexed(viewModel.filterValues.itemSizes.sorted()) { index: Int, ct ->
                            Log.d(TAG, "FiltersScreen: itemsize: $ct")
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
                            Spacer(modifier = Modifier.width(2.dp))
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
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {

                OutlinedButton(
                    border = BorderStroke(width = 1.dp, MaterialTheme.colors.onPrimary),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colors.primary
                    ),
                    onClick = {
                        val emptyFilters = ClothesFilter().apply {
                            price = viewModel.filterValues.price
                        }
                        viewModel.onTriggerEvent(ShopEvent.ChangeCustomFilters(emptyFilters))
                    },
                    shape = CircleShape,
                ) {
                    Text(
                        text = "Сбросить",
                        fontSize = TextUnit(value = 21f, TextUnitType.Sp),
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
                        customFilter.price.max?.let { maxPrice ->
                            isMinPriceLowerThenMax = customFilter.price.min < maxPrice
                        }
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
                        fontSize = TextUnit(value = 21f, TextUnitType.Sp),
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

@OptIn(ExperimentalMaterialApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun BrandsModalSheet(
    sheetState: ModalBottomSheetState,
    brands: List<Brand>
) {
    ModalBottomSheetLayout(
        sheetContent = {
            LazyVerticalGrid(
                modifier = Modifier,
                cells = GridCells.Fixed(3),
            ) {
                itemsIndexed(brands){index, item ->
                    item.image?.let { image ->
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .width(83.dp)
                                    .height(38.dp),
                                bitmap = image.asImageBitmap(),
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Checkbox(
                                checked = false,
                                onCheckedChange = {

                                }
                            )
                        }
                    }
                }
            }
        },
        sheetState = sheetState
    ) {
        
    }

}


















