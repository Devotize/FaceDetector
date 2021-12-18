package com.sychev.facedetector.presentation.ui.screen.own_image.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.utils.toMoneyString

@Composable
fun FoundedClothesCardExtended(
    modifier: Modifier = Modifier,
    foundedClothes: List<Clothes>,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onGenderChange: (String) -> Unit,
) {
    val clothes = foundedClothes[0]
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
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row() {
                val painter = rememberImagePainter(data = clothes.picUrl) {
                    crossfade(true)
                    error(R.drawable.clothes_default_icon_gray)
                }
                Image(
                    modifier = Modifier
                        .padding(start = 17.dp, top = 7.dp, bottom = 7.dp),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                )
                Column(modifier = Modifier.fillMaxHeight()) {
                    Spacer(modifier = Modifier.height(10.dp))
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
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        text = text,
                        maxLines = 1,
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    //switch gender
                    Row(
                        modifier = Modifier.padding(start = 28.dp)
                    ) {
                        //male toggle button
                        val isMale = clothes.gender == FilterValues.Constants.Gender.male
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    val newGender = FilterValues.Constants.Gender.male
                                    onGenderChange(newGender)
                                },
                            color = if (isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            border = BorderStroke(
                                1.dp,
                                if (!isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            ),
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
                            border = BorderStroke(
                                1.dp,
                                if (isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            ),
                            shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Female,
                                contentDescription = null,
                                tint = if (isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    // rating
                    Row(modifier = Modifier.padding(start = 16.dp)) {
                        Icon(
                            modifier = Modifier.size(7.dp),
                            imageVector = Icons.Default.Star,
                            tint = Color.Yellow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Text(
                            modifier = Modifier,
                            text = clothes.rating.toString(),
                            fontWeight = FontWeight.W400,
                            fontSize = 7.sp,
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxHeight()
                    .width(113.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                foundedClothes.forEach { item ->
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
                            fontSize = 9.sp,
                            fontWeight = FontWeight.W500
                        )
                        Text(
                            modifier = Modifier,
                            text = "${item.price.toString().toMoneyString()} ₽ >",
                            maxLines = 1,
                            color = MaterialTheme.colors.primaryVariant,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.W500
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Column(
                modifier = Modifier
                    .padding(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    modifier = Modifier.clickable {
                        onCloseClick()
                    },
                    imageVector = Icons.Default.Close,
                    tint = MaterialTheme.colors.primary,
                    contentDescription = null,
                )
            }
        }
    }
}