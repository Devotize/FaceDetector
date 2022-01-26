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
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.utils.*

@Composable
fun ClothesBigItem(
    modifier: Modifier = Modifier,
    clothes: Clothes,
    clothesList: List<Clothes>,
    onAddToFavoriteClick: (Clothes) -> Unit,
    onRemoveFromFavoriteClick: (Clothes) -> Unit,
    onShoppingCartClick: () -> Unit = {},
    onShareClick: () -> Unit,
) {

    Card(
        modifier = modifier,
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp, start = 24.dp, end = 24.dp)
        ) {
            val imagePainter = rememberImagePainter(data = clothes.picUrl) {
                crossfade(true)
                error(R.drawable.clothes_default_icon_gray)
            }
            Surface(
                modifier = Modifier
                    .wrapContentSize(),
                shape = MaterialTheme.shapes.large,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(214.dp),
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier) {
                        Text(
                            modifier = Modifier,
                            text = "${clothes.itemCategory} ${clothes.brand}",
                            style = MaterialTheme.typography.h5,
                            color = Color.Black
                        )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        modifier = Modifier,
                        text = "${clothes.price.toString().toMoneyString()} ₽",
                        style = MaterialTheme.typography.h3,
                        maxLines = 1,
                        color = MaterialTheme.colors.onSurface
                    )
                }
                clothes.brandLogo.let {
                    if (it.isNotEmpty()) {
                        Image(
                            modifier = Modifier
                                .size(62.dp),
                            bitmap = clothes.brandLogo.decodeToBitmap().asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Column() {
                    if (clothes.rating > 0.0) {
                        Rating(
                            modifier = Modifier,
                            rating = clothes.rating
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    IconButton(
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp),
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
            Row(modifier = Modifier.animateContentSize(spring(1.75f))) {
                Spacer(modifier = Modifier.height(4.dp))
                clothesList.forEach {
                    Spacer(modifier = Modifier.width(6.dp))
                    ShopComponent(
                        modifier = Modifier.width(72.dp),
                        clothes = it,
                        onShoppingCartClick = onShoppingCartClick
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Composable
fun ShopComponent(
    modifier: Modifier = Modifier,
    clothes: Clothes,
    onShoppingCartClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = clothes.provider,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.align(Alignment.Bottom),
                text = "${clothes.price.toString().toMoneyString()} ₽",
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colors.onPrimary
            )
            IconButton(
                modifier = Modifier
                    .size(18.dp),
                onClick = onShoppingCartClick,
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}