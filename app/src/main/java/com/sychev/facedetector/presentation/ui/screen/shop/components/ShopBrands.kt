package com.sychev.facedetector.presentation.ui.screen.shop.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.presentation.ui.screen.shop.ClothesFilter
import com.sychev.facedetector.presentation.ui.screen.shop.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop.ShopViewModel

@Composable
fun ShopBrands(
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
            if (topBrands.isEmpty()) {
                repeat(10) {
                    item {
                        if (it == 0) {
                            Spacer(modifier = Modifier.width(28.dp))
                        }
                        Column(
                            modifier = Modifier
                                .padding(end = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        Surface(
                            modifier = Modifier.size(62.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colors.background
                        ) {

                        }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(62.dp)
                                    .height(12.dp)
                                    .background(MaterialTheme.colors.background, MaterialTheme.shapes.large),
                            )
                        }
                    }
                }
            }
            itemsIndexed(topBrands) { index: Int, item: Brand ->
                if (index == 0) {
                    Spacer(modifier = Modifier.width(28.dp))
                }
                item.image?.let {
                    Column(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clickable {
                                val newFilter = ClothesFilter().apply {
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