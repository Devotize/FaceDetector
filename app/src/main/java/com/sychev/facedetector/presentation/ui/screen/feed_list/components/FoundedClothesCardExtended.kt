package com.sychev.facedetector.presentation.ui.screen.feed_list.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.presentation.ui.screen.feed_list.FoundedClothes
import com.sychev.facedetector.presentation.ui.screen.feed_list.FoundedClothesExtended
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.toMoneyString

@Composable
fun FoundedClothesCardExtended(
    modifier: Modifier = Modifier,
    foundedClothes: FoundedClothesExtended,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onGenderChange: (String) -> Unit,
) {
    val clothes = foundedClothes.clothes[0]
    val imageWidthDp = 62.dp
    val imageHeightDp = 82.dp
    Surface(
        modifier = modifier
            .clickable {
                       onClick()
            },
        elevation = 0.dp,
        color = Color.Black.copy(alpha = 0.84f),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp)

        ) {
            val painter = rememberImagePainter(data = clothes.picUrl) {
                crossfade(true)
                error(R.drawable.clothes_default_icon_gray)
            }
            Image(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(imageWidthDp)
                    .height(imageHeightDp),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Spacer(modifier = Modifier.height(2.dp))
                val fullStr = "${clothes.itemCategory} ${clothes.brand}"
                var text = ""
                kotlin.run let@{
                    fullStr.forEach { char ->
                        text += char
                        if (text.length > 15) {
                            text += "..."
                            return@let
                        }
                    }
                }
                var titleWidthDp by remember {
                    mutableStateOf(0.dp)
                }
                var titleWidthPx by remember { mutableStateOf(0)}.also {
                    titleWidthDp = with(LocalDensity.current){it.value.toDp()}

                }
                Text(
                    modifier = Modifier
                        .onGloballyPositioned {
                            titleWidthPx = it.size.width
                        },
                    text = text,
                    maxLines = 1,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .width(113.dp),
                ) {
                    itemsIndexed(foundedClothes.clothes) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier,
                                text = item.provider,
                                maxLines = 1,
                                color = MaterialTheme.colors.primary,
                                style = MaterialTheme.typography.caption
                            )
                            Text(
                                modifier = Modifier,
                                text = "${item.price.toString().toMoneyString()} ₽",
                                maxLines = 1,
                                color = MaterialTheme.colors.primaryVariant,
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }
                    item {
                        Text(
                            modifier = Modifier,
                            text = "...",
                            maxLines = 1,
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    modifier = Modifier.clickable{
                                                 onCloseClick()
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(28.dp))
                Row {
                    //male toggle button
                    val isMale = clothes.gender == FilterValues.Constants.Gender.male
                    Surface(
                        modifier = Modifier
                            .clickable {
                                val newGender = FilterValues.Constants.Gender.male
                                onGenderChange(newGender)
                            },
                        color = if (isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                        border = BorderStroke(1.dp , if (!isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,),
                        shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Male,
                            contentDescription = null,
                            tint = if (!isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                        )
                    }
                    //female
                    Surface(
                        modifier = Modifier
                            .clickable {
                                val newGender = FilterValues.Constants.Gender.female
                                onGenderChange(newGender)
                            },
                        color = if (!isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                        border = BorderStroke(1.dp , if (isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,),
                        shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Female,
                            contentDescription = null,
                            tint = if (isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                        )
                    }
                }
            }
        }
    }
}