package com.sychev.facedetector.presentation.ui.components

import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.utils.loadPicture
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toMoneyString

@Composable
fun ClothesItem(
    modifier: Modifier = Modifier,
    clothes: Clothes,
    shape: Shape = MaterialTheme.shapes.medium,
    onAddToFavoriteClick: (Clothes) -> Unit,
    onRemoveFromFavoriteClick: (Clothes) -> Unit,
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier,
        shape = shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp, start = 14.dp, end = 14.dp),
        ) {
                Surface(modifier = Modifier
                    .width(110.dp)
                    .height(140.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    val imagePainter = rememberImagePainter(data = clothes.picUrl){
                        crossfade(true)
                        error(R.drawable.clothes_default_icon_gray)
                    }
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = imagePainter,
                        contentDescription = null,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop
                    )
                }

            Column(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .height(140.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
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
                        ,
                    text = "${clothes.price.toString().toMoneyString()} ₽",
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h4,
                )

                Row(
                    modifier = Modifier
                    ,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Rating(
                        rating = clothes.rating,
                        textStyle = MaterialTheme.typography.subtitle1,
                        starSize = 18.dp,
                    )
                }

                Text(
                    modifier = Modifier,
                    text = "${clothes.provider}",
                    color = MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.subtitle1
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Bottom),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    if (clothes.brandLogo.isNotBlank()) {
                        val logoBytes = Base64.decode(clothes.brandLogo, 0)
                        Image(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(40.dp),
                            bitmap = logoBytes.toBitmap().asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    IconButton(
                        modifier = Modifier
                            .wrapContentSize(),
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
                }
            }
        }
    }
}














