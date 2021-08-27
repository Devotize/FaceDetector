package com.sychev.facedetector.presentation.ui.screen.clothes_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes

@Composable
fun ClothesDetailScreen(
    clothes: Clothes
){
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(
                state = scrollState
            )
            .padding(top = 8.dp, start = 16.dp),
    ) {
        Text(
            text = clothes.brand.plus(" ${clothes.itemCategory}"),
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.onPrimary
        )
        Text(
            text = clothes.provider,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.primaryVariant
        )
        val painterState = rememberImagePainter(data = clothes.picUrl){
            error(R.drawable.clothes_default_icon_gray)
            crossfade(true)
            transformations(RoundedCornersTransformation(8.dp.value))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Image(
                modifier = Modifier
                    .width(152.dp)
                    .height(224.dp)
                    .padding(top = 6.dp),
                painter = painterState,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .height(224.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = clothes.price.toString().plus(" р."),
                    style = MaterialTheme.typography.h2,
                    color = MaterialTheme.colors.onPrimary
                )
                RatingBar(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background),
                    value = clothes.rating.toFloat(),
                    numStars = 5,
                    size = 18.dp,
                    activeColor = MaterialTheme.colors.secondary,
                    inactiveColor = MaterialTheme.colors.primaryVariant,
                    isIndicator = true,
                    ratingBarStyle = RatingBarStyle.Normal,
                    onRatingChanged = {

                    }
                )
                Row(
                   modifier = Modifier,
                ) {
                    IconButton(
                        modifier = Modifier
                            .wrapContentSize(),
                        onClick = {
                            if (clothes.isFavorite) {

                            } else {

                            }
                        },
                    ) {
                        Icon(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            imageVector = if (clothes.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (clothes.isFavorite) Color.Red else MaterialTheme.colors.primaryVariant
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .width(8.dp),
                    )
                    IconButton(
                        modifier = Modifier
                            .wrapContentSize(),
                        onClick = {
                            if (clothes.isFavorite) {

                            } else {

                            }
                        },
                    ) {
                        Icon(
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp),
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primaryVariant
                        )
                    }
                }
                Button(
                    onClick = {

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary
                    )
                ) {
                    Text(
                        text = "Buy",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.primary
                    )

                }
            }
        }
        Spacer(modifier = Modifier
            .width(2.dp)
            .height(16.dp))
        Text(
            text = "Similar clothes",
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.onPrimary,
        )

        
    }
}













