package com.sychev.facedetector.presentation.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.utils.toMoneyString

@Composable
fun SimilarClothesCard(
    modifier: Modifier = Modifier,
    clothes: Clothes,
    onShoppingCartClick: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
    ) {
        val painter = rememberImagePainter(data = clothes.picUrl) {
            crossfade(true)
            error(R.drawable.clothes_default_icon_gray)
        }
        Image(
            modifier = Modifier
                .width(94.dp)
                .height(144.dp),
            painter = painter,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(4.dp))
        val fullStr = "${clothes.itemCategory} ${clothes.brand}"
        var text = ""
        kotlin.run let@{
            fullStr.forEach { char ->
                text += char
                if (text.length > 30) {
                    text += "..."
                    return@let
                }
            }
        }
        Text(
            modifier = Modifier
                .widthIn(max = 100.dp)
                .height(25.dp),
            text = text,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 2,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.width(94.dp),
        ) {
            Column() {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = clothes.price.toString().toMoneyString().plus(" ₽"),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.subtitle2
                )

                Spacer(modifier = Modifier.height(4.dp))
                if (clothes.rating > 0.0) {
                    Rating(rating = clothes.rating)
                }

            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    ,
            ) {
                IconButton(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Center),
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

    }
}
