package com.sychev.facedetector.presentation.ui.components

import android.content.Intent
import android.util.Base64
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.utils.loadPicture
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toMoneyString

@Composable
fun ClothesBigItem(
    clothes: Clothes,
    onAddToFavoriteClick: (Clothes) -> Unit,
    onRemoveFromFavoriteClick: (Clothes) -> Unit,
    onShoppingCartClick: () -> Unit = {},
    onShareClick: () -> Unit,
) {
    val showDetails = remember{ mutableStateOf(false) }
    val context = LocalContext.current


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            val imagePainter = rememberImagePainter(data = clothes.picUrl) {
                crossfade(true)
                error(R.drawable.clothes_default_icon_gray)
            }
                Surface(
                    modifier = Modifier
                        .wrapContentSize(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colors.background
                ){
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(188.dp),
                        painter = imagePainter,
                        contentDescription = null,
                    )
                }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${clothes.itemCategory} ${clothes.brand}",
                    style = MaterialTheme.typography.h3,
                    color = Color.Black
                )

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
            }
            Row(
                modifier = Modifier,
            ) {
                Text(
                    modifier = Modifier
                        .padding(0.dp,4.dp,0.dp,0.dp),
                    text = "${clothes.price.toString().toMoneyString()} ₽",
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.onSurface
                )
                if (clothes.brandLogo.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    val bytes = Base64.decode(clothes.brandLogo, 0)
                    Image(
                        modifier = Modifier
                            .width(440.dp)
                            .height(50.dp),
                        bitmap = bytes.toBitmap().asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
            Column(modifier = Modifier.animateContentSize(spring(1.75f))) {
                if (showDetails.value) {
                    Text(
                        text = clothes.brand,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    listOf(Shop(name = clothes.provider),Shop(name = clothes.provider),Shop(name = clothes.provider)).forEach {
                        ShopComponent(shop = it, onShoppingCartClick = onShoppingCartClick)
                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .clickable {
                            showDetails.value = !showDetails.value
                        },
                    text = if (!showDetails.value) "Details" else "Hide",
                    style = MaterialTheme.typography.subtitle2,
                )

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
                            if (clothes.isFavorite) {
                                onRemoveFromFavoriteClick(clothes)
                            } else {
                                onAddToFavoriteClick(clothes)
                            }
                        },
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (clothes.isFavorite) Color.Red else MaterialTheme.colors.primaryVariant
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .padding(4.dp, 0.dp, 0.dp, 0.dp),
                        onClick = {
                            onShareClick()
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
}

@Composable
fun ShopComponent(
    shop: Shop,
    onShoppingCartClick: () -> Unit
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
                    onClick = {
                              onShoppingCartClick()
                    },
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
