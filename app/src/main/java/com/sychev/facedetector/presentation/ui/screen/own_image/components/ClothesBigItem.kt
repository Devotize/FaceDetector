package com.sychev.facedetector.presentation.ui.screen.own_image.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.utils.toMoneyString


@Composable
fun ClothesBigItem(
    modifier: Modifier = Modifier,
    clothes: Clothes,
    clothesList: List<Clothes>,
    onShoppingCartClick: () -> Unit = {},
    onShareClick: () -> Unit,
) {
    val showDetails = remember { mutableStateOf(false) }
    val context = LocalContext.current


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
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.fillMaxWidth(0.6f)) {
                    Text(
                        modifier = Modifier,
                        text = "${clothes.itemCategory} ${clothes.brand}    ${clothes.price.toString().toMoneyString()} ₽",
                        style = MaterialTheme.typography.h6,
                        color = Color.Black
                    )
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text(
//                        modifier = Modifier,
//                        text = "${clothes.price.toString().toMoneyString()} ₽",
//                        style = MaterialTheme.typography.h6,
//                        maxLines = 1,
//                        color = MaterialTheme.colors.onSurface
//                    )
                }
                    Row(
                        modifier = Modifier.wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
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
                        Spacer(modifier = Modifier.width(8.dp))
                        if (clothes.rating > 0.0) {
                            Rating(rating = clothes.rating)
                        }


                    }
                    
            }
            //price
            
            Row(modifier = Modifier.animateContentSize(spring(1.75f))) {
//                if (showDetails.value) {
                Spacer(modifier = Modifier.height(4.dp))
                clothesList.forEach {
                    Spacer(modifier = Modifier.width(6.dp))
                    ShopComponent(
                        clothes = it,
                        onShoppingCartClick = onShoppingCartClick
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
//                }
            }
            Spacer(modifier = Modifier.height(8.dp))

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Text(
//                    modifier = Modifier
//                        .clickable {
//                            showDetails.value = !showDetails.value
//                        },
//                    text = if (!showDetails.value) "Ещё" else "Скрыть",
//                    style = MaterialTheme.typography.subtitle2,
//                )
//
//            }

        }
    }
}

@Composable
fun ShopComponent(
    clothes: Clothes,
    onShoppingCartClick: () -> Unit
) {
    Column {
        Text(
            text = clothes.provider,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${clothes.price.toString().toMoneyString()} ₽",
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        IconButton(
            modifier = Modifier
                .size(18.dp),
            onClick = onShoppingCartClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}

//data class Shop(
//    val name: String = "Wildberries",
//    val price: String = "11 549 ₽",
//    val size: String = "S, M, L, XL, XXL",
//)
