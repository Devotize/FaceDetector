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
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.loadPicture

@Composable
fun ClothesItem(
    detectedClothes: DetectedClothes,
    onAddToFavoriteClick: (DetectedClothes) -> Unit,
    onRemoveFromFavoriteClick: (DetectedClothes) -> Unit,
) {
    val showDetails = remember{mutableStateOf(false)}
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp, 8.dp, 16.dp, 8.dp),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(8.dp, 8.dp, 2.dp, 0.dp),
            ) {
                loadPicture(
                    url = detectedClothes.picUrl,
                    defaultImage = R.drawable.clothes_default_icon
                ).value?.let {
                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(8.dp, 8.dp, 8.dp, 4.dp),
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
                    ) {
                        Image(
                            modifier = Modifier
                                .width(96.dp)
                                .height(96.dp),
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp, 8.dp, 8.dp, 4.dp),
                ) {
                    Text(
                        text = detectedClothes.brand,
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.Black
                    )
                    Text(
                        modifier = Modifier
                            .padding(0.dp,4.dp,0.dp,0.dp),
                        text = "12 000 ₽",
                        style = MaterialTheme.typography.h2,
                        color = MaterialTheme.colors.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            color = MaterialTheme.colors.onSurface,
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.wrapContentSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(8.dp, 2.dp, 0.dp, 2.dp)
                                        .width(15.dp)
                                        .height(15.dp),
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.Yellow
                                )
                                Text(
                                    modifier = Modifier.padding(2.dp, 2.dp, 8.dp, 2.dp),
                                    text = "4.7",
                                    color = Color.White,
                                    style = MaterialTheme.typography.subtitle2
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp)
                                    .padding(0.dp, 0.dp, 0.dp, 4.dp),
                                onClick = {
                                    if (detectedClothes.isFavorite) {
                                        onRemoveFromFavoriteClick(detectedClothes)
                                    } else {
                                        onAddToFavoriteClick(detectedClothes)
                                    }
                                },
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = if (detectedClothes.isFavorite) Color.Red else MaterialTheme.colors.primaryVariant
                                )
                            }
                            IconButton(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp)
                                    .padding(4.dp, 0.dp, 0.dp, 0.dp),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply{
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Sharing Url")
                                        putExtra(Intent.EXTRA_TEXT, detectedClothes.url)
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primaryVariant
                                )
                            }
                        }
                    }

                }
            }
            Column(modifier = Modifier
                    .animateContentSize(spring(1.75f))
                    .padding(16.dp, 8.dp, 16.dp, 8.dp)
                ) {
                if (showDetails.value) {
                    Text(
//                        modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 8.dp),
                        text = detectedClothes.brand,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface,
                    )

                    listOf(Shop(),Shop(),Shop()).forEach {
                        ShopComponent(shop = it)
                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onClick = {
                          showDetails.value = !showDetails.value
                },
            ){
                Text(
                    modifier = Modifier,
                    text = if (!showDetails.value) "Details" else "Hide",
                    style = MaterialTheme.typography.subtitle2,
                )
            }


        }

    }
}


@Composable
fun ShopComponent(
    shop: Shop
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.70f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 8.dp, 8.dp, 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shop.name,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = shop.price,
                        style = MaterialTheme.typography.overline,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 0.dp, 8.dp, 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Size",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = shop.size,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 0.dp, 8.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onBackground
                    )
                    Row {
                        OutlinedButton(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp)
                                .clickable {},
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                        ){
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp)
                                .clickable {},
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF8C8C))
                        ){
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp)
                                .clickable {},
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00790C))
                        ){
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            modifier = Modifier
                                .width(15.dp)
                                .height(15.dp)
                                .clickable {},
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00A3FF))
                        ){
                        }

                    }
                }

            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterVertically)) {
                Button(
                    modifier = Modifier
                        .width(45.dp)
                        .height(45.dp)
                        .align(Alignment.Center),
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.onPrimary,
                        contentColor = MaterialTheme.colors.primary
                    ),
                    shape = CircleShape,
                    contentPadding = PaddingValues(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                    )
                }
            }
        }

    }
}

data class Shop(
    val name: String = "Wildberries",
    val price: String = "11 549 ₽",
    val size: String = "S, M, L, XL, XXL",
)














