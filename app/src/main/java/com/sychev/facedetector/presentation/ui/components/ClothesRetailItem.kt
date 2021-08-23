package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.utils.loadPicture

@Composable
fun ClothesRetailItem(
    modifier: Modifier = Modifier,
    clothes: Clothes,
    onAddToFavoriteClick: (Clothes) -> Unit,
    onRemoveFromFavoriteClick: (Clothes) -> Unit,
) {
    val rating = remember{ mutableStateOf(clothes.rating.toFloat())}
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 6.dp, start = 6.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Bottom),
                    onClick = {
                        if (clothes.isFavorite) {
                            onRemoveFromFavoriteClick(clothes)
                        } else {
                            onAddToFavoriteClick(clothes)
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
                Text(
                    text = clothes.provider,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primaryVariant
                )
            }
            loadPicture(url = clothes.picUrl, defaultImage = R.drawable.clothes_default_icon).value?.let{ bitmap ->
                Image(
                    modifier = Modifier
                        .width(185.dp)
                        .height(248.dp)
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp, start = 8.dp, end = 8.dp,),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column() {
                    Text(
                        modifier = Modifier
                            .widthIn(max = 150.dp)
                            .heightIn(max = 47.dp),
                        text = "${clothes.brand} ${clothes.itemCategory}",
                        color = MaterialTheme.colors.onPrimary,
                        style = MaterialTheme.typography.overline
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = "${clothes.price} р.",
                        color = MaterialTheme.colors.onPrimary,
                        style = MaterialTheme.typography.h2,
                    )

                    Row(
                        modifier = Modifier
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBar(
                            modifier = Modifier,
                            value = rating.value,
                            numStars = 5,
                            size = 18.dp,
                            activeColor = MaterialTheme.colors.secondary,
                            inactiveColor = MaterialTheme.colors.primaryVariant,
                            isIndicator = true,
                            ratingBarStyle = RatingBarStyle.Normal,
                            onRatingChanged = {

                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 6.dp),
                            text = rating.value.toString(),
                            color = MaterialTheme.colors.primaryVariant,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .size(56.dp),
                    onClick = {

                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                    )
                }

            }
        }
    }

}