package com.sychev.facedetector.presentation.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.loadPicture

@Composable
fun ClothesItem(
    modifier: Modifier = Modifier,
    detectedClothes: DetectedClothes,
    onAddToFavoriteClick: (DetectedClothes) -> Unit,
    onRemoveFromFavoriteClick: (DetectedClothes) -> Unit,
) {
    val context = LocalContext.current
    val rating = remember{mutableStateOf(4f)}

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp, start = 14.dp, end = 14.dp),
        ) {
            loadPicture(url = detectedClothes.picUrl, defaultImage = R.drawable.clothes_default_icon).value?.let{bitmap ->
                Surface(modifier = Modifier
                    .width(110.dp)
                    .height(140.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .widthIn(max = 150.dp)
                        .heightIn(max = 47.dp),
                    text = "${detectedClothes.brand} ${detectedClothes.itemCategory}",
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.overline
                )
                Text(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    text = "20 700 р.",
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

                Text(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    text = "Lamoda",
                    color = MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.subtitle1
                )

                Text(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    text = "32 comments",
                    color = MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.subtitle2
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Bottom),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Bottom),
                    onClick = {
                        if (detectedClothes.isFavorite) {
                            onRemoveFromFavoriteClick(detectedClothes)
                        } else {
                            onAddToFavoriteClick(detectedClothes)
                        }
                    },
                ) {
                    Icon(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        imageVector = if (detectedClothes.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (detectedClothes.isFavorite) Color.Red else MaterialTheme.colors.primaryVariant
                    )
                }
            }
        }
    }
}














