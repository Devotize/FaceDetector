package com.sychev.facedetector.presentation.ui.screen.shop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.presentation.ui.screen.shop.ClothesFilter
import com.sychev.facedetector.presentation.ui.screen.shop.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop.ShopViewModel

@Composable
fun ShopSearchBar(
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
                                    ClothesFilter().apply {
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
                            style = MaterialTheme.typography.h5,
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
                            style = MaterialTheme.typography.h5,
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
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}