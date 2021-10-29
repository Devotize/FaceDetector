package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.sychev.facedetector.R
import com.sychev.facedetector.presentation.ui.screen.feed_list.FeedViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ShopEvent
import com.sychev.facedetector.presentation.ui.screen.shop_screen.TestClothesFilter
import com.sychev.facedetector.utils.toMoneyString

@ExperimentalMaterialApi
@Composable
fun FoundedClothesCard(
    modifier: Modifier = Modifier,
    foundedClothes: FeedViewModel.FoundedClothes,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onGenderChange: (newGender: String) -> Unit,
){
    val clothes = foundedClothes.clothes[0]
    val x = with(LocalDensity.current) {foundedClothes.location.centerX().toDp()}
    val y = with(LocalDensity.current) {foundedClothes.location.centerY().toDp()}
    val width = 295.dp
    val height = 95.dp
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(width)
                .height(height),
            shape = RoundedCornerShape(6.dp),
            onClick = {
                onClick()
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .wrapContentSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val imagePainter = rememberImagePainter(
                        data = clothes.picUrl,
                        builder = {
                            crossfade(true)
                            transformations(RoundedCornersTransformation(6.dp.value))
                            error(R.drawable.clothes_default_icon_gray)
                        }
                    )
                    Image(
                        modifier = Modifier
                            .width(68.dp)
                            .fillMaxHeight()
                            ,
                        painter = imagePainter,
                        contentDescription = null,
                    )
                }
                Column(
                    modifier = Modifier
                        .widthIn(max = 128.dp)
                        .fillMaxHeight()
                        .padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.heightIn(max = 50.dp),
                        text = "${clothes.itemCategory} ${clothes.brand} ",
                        style = MaterialTheme.typography.subtitle2,
                        color = Color.Black
                    )
                    Text(
                        text = "${clothes.price.toString().toMoneyString()} ₽",
                        style = MaterialTheme.typography.h2,
                    )

                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Surface(
                        modifier = Modifier
                            .height(24.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colors.onPrimary
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 4.dp, start =  10.dp, end = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.StarRate,
                                contentDescription = null,
                                tint = MaterialTheme.colors.secondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${clothes.rating}",
                                color = Color.White,
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                    }
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
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.Default.Male,
                                contentDescription = null,
                                tint = if (!isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))
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
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.Default.Female,
                                contentDescription = null,
                                tint = if (isMale) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        FloatingActionButton(
            modifier = Modifier,
            shape = CircleShape,
            onClick = onCloseClick,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}