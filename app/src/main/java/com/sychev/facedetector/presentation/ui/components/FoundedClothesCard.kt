package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.sychev.facedetector.R
import com.sychev.facedetector.presentation.ui.screen.feed_list.FeedViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color

@ExperimentalMaterialApi
@Composable
fun FoundedClothesCard(
    foundedClothes: FeedViewModel.FoundedClothes,
    onClick: () -> Unit,
    onCloseClick: () -> Unit
){
    val clothes = foundedClothes.clothes[0]
    val x = with(LocalDensity.current) {foundedClothes.location.centerX().toDp()}
    val y = with(LocalDensity.current) {foundedClothes.location.centerY().toDp()}
    val width = 240.dp
    val height = 90.dp
    Surface(
        modifier = Modifier
            .width(width)
            .height(height)
            .offset(x = x - width / 2, y = y - height - 8.dp),
        shape = RoundedCornerShape(6.dp),
        onClick = {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .width(75.dp)
                    .fillMaxHeight(),
                painter = rememberImagePainter(
                    data = clothes.picUrl,
                    builder = {
                        crossfade(true)
                        transformations(RoundedCornersTransformation(6.dp.value))
                        error(R.drawable.clothes_default_icon_gray)
                    }
                ),
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .widthIn(max = 90.dp)
                    .fillMaxHeight()
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${clothes.brand} ${clothes.itemCategory}",
                    style = MaterialTheme.typography.subtitle2,
                    color = Color.Black
                )
                Text(
                    text = "${clothes.price} ₽",
                    style = MaterialTheme.typography.h2,
                )

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    modifier = Modifier
                        .size(24.dp),
                    onClick = {
                        onCloseClick()
                    }
                ){
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = null)
                }

                Surface(
                    modifier = Modifier
                        .height(24.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colors.onPrimary
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp, bottom = 4.dp, start =  10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.StarRate,
                            contentDescription = null,
                            tint = MaterialTheme.colors.secondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${clothes.rating}",
                            color = Color.White,
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                }

            }
        }
    }
}