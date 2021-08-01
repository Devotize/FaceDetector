package com.sychev.facedetector.presentation.ui.components

import android.content.Intent
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.utils.loadPicture

@Composable
fun ClothesBigItem(
    detectedClothes: DetectedClothes,
    onAddToFavoriteClick: (DetectedClothes) -> Unit,
    onRemoveFromFavoriteClick: (DetectedClothes) -> Unit,
) {
    val showDetails = remember{ mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp, 8.dp, 16.dp, 8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            loadPicture(url = detectedClothes.picUrl, defaultImage = R.drawable.clothes_default_icon).value?.let {
                Surface(
                    modifier = Modifier
                        .wrapContentSize(),
                    shape = MaterialTheme.shapes.large,
                    border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
                ){
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(168.dp),
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = detectedClothes.brand,
                    style = MaterialTheme.typography.subtitle1,
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
            Text(
                modifier = Modifier
                    .padding(0.dp,4.dp,0.dp,0.dp),
                text = "12 000 ₽",
                style = MaterialTheme.typography.h2,
                color = MaterialTheme.colors.onSurface
            )

            Column(modifier = Modifier.animateContentSize(spring(1.75f))) {
                if (showDetails.value) {
                    Text(
                        text = detectedClothes.brand,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface,
                    )

//                    listOf(Shop(),Shop(),Shop()).forEach {
//                        ShopComponent(shop = it)
//                        Spacer(modifier = Modifier.padding(bottom = 8.dp))
//                    }
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
}