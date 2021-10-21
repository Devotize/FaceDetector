package com.sychev.facedetector.presentation.ui.screen.clothes_detail

import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.utils.toBitmap
import com.sychev.facedetector.utils.toMoneyString


@Composable
fun ClothesDetailScreen(
    clothes: Clothes,
    viewModel: ClothesDetailViewModel,
    onBackClick: () -> Unit
){
    val clothesFromCache = viewModel.clothes.value
    val scrollState = rememberScrollState()
    var firstLaunch by remember{mutableStateOf(true)}
    if (firstLaunch) {
        viewModel.onTriggerEvent(ClothesDetailEvent.GetClothesFromCache(clothes))

        viewModel.onTriggerEvent(
            ClothesDetailEvent.SearchSimilarClothesEvent(
                clothes = clothes,
                context = LocalContext.current,
            )
        )
        firstLaunch = false
    }
    val similarClothes = viewModel.similarClothes
    val context = LocalContext.current
    clothesFromCache?.let { cl ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.primary)
                .verticalScroll(
                    state = scrollState
                )
                .padding(top = 8.dp, start = 16.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier
                        .size(30.dp),
                    onClick = {
                        onBackClick()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                Column {
                    Text(
                        text = cl.brand.plus(" ${cl.itemCategory}"),
                        style = MaterialTheme.typography.h2,
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(
                        text = cl.provider,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primaryVariant
                    )
                }

            }

            val painterState = rememberImagePainter(data = cl.picUrl){
                error(R.drawable.clothes_default_icon_gray)
                crossfade(true)
                transformations(RoundedCornersTransformation(8.dp.value))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                Image(
                    modifier = Modifier
                        .width(152.dp)
                        .height(224.dp)
                        .padding(top = 6.dp),
                    painter = painterState,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )

                Column(
                    modifier = Modifier
                        .height(224.dp)
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 2.dp, top = 2.dp,),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (cl.brandLogo.isNotEmpty()) {
                        val byteString = Base64.decode(cl.brandLogo, 0)
                        val bitmap = byteString.toBitmap()
                        val logoPainter = rememberImagePainter(data = bitmap){
                            crossfade(true)
                            error(R.drawable.default_logo_icon)
                        }
                        Image(
                            modifier = Modifier
                                .width(90.dp)
                                .height(40.dp),
                            painter = logoPainter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                    }


                    Text(
                        text = cl.price.toString().toMoneyString().plus(" ₽"),
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.onPrimary
                    )
                    RatingBar(
                        modifier = Modifier
                            .background(MaterialTheme.colors.primary),
                        value = cl.rating.toFloat(),
                        numStars = 5,
                        size = 22.dp,
                        activeColor = MaterialTheme.colors.secondary,
                        inactiveColor = MaterialTheme.colors.primaryVariant,
                        isIndicator = true,
                        ratingBarStyle = RatingBarStyle.Normal,
                        onRatingChanged = {

                        }
                    )
                    Row(
                        modifier = Modifier,
                    ) {
                        IconButton(
                            modifier = Modifier
                                .wrapContentSize(),
                            onClick = {
                                if (cl.isFavorite) {
                                    viewModel.onTriggerEvent(ClothesDetailEvent.RemoveFromFavoriteClothesEvent(cl))
                                } else {
                                    viewModel.onTriggerEvent(ClothesDetailEvent.AddToFavoriteClothesEvent(cl))
                                }
                            },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp),
                                imageVector = if (cl.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (cl.isFavorite) Color.Red else MaterialTheme.colors.primaryVariant
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(2.dp)
                                .width(8.dp),
                        )
                        IconButton(
                            modifier = Modifier
                                .wrapContentSize(),
                            onClick = {
                                      viewModel.onTriggerEvent(ClothesDetailEvent.ShareClothesEvent(context, cl))
                            },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp),
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primaryVariant
                            )
                        }
                    }
                    Button(
                        onClick = {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(cl.clothesUrl))
                            startActivity(context, browserIntent, null)
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary
                        )
                    ) {
                        Text(
                            text = "Купить",
                            style = MaterialTheme.typography.button,
                            color = MaterialTheme.colors.onPrimary
                        )

                    }
                }
            }
            Spacer(modifier = Modifier
                .width(2.dp)
                .height(16.dp))
            Text(
                text = "Похожие товары",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.onPrimary,
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier,
                contentPadding = PaddingValues(6.dp)
            ){
                itemsIndexed(similarClothes) {index, item ->
                    SimilarClothesCard(
                        modifier = Modifier
                            .clickable {
                                viewModel.onTriggerEvent(ClothesDetailEvent.GoToDetailScreen(item))
                            }
                            .padding(end = 12.dp),
                        clothes = item
                    )
                }
            }

        }
    }
}


@Composable
fun SimilarClothesCard(
    modifier: Modifier = Modifier,
    clothes: Clothes
) {
    Column(
        modifier = modifier
    ) {
        val painter = rememberImagePainter(data = clothes.picUrl){
            crossfade(true)
            error(R.drawable.clothes_default_icon_gray)
        }
        Image(
            modifier = Modifier
                .width(114.dp)
                .height(164.dp),
            painter = painter,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier
                .widthIn(max = 114.dp),
            text = "${clothes.brand} ${clothes.itemCategory}",
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(
            modifier = Modifier
                .background(MaterialTheme.colors.primary ),
            value = clothes.rating.toFloat(),
            numStars = 5,
            size = 16.dp,
            activeColor = MaterialTheme.colors.secondary,
            inactiveColor = MaterialTheme.colors.primaryVariant,
            isIndicator = true,
            ratingBarStyle = RatingBarStyle.Normal,
            onRatingChanged = {

            }
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = clothes.price.toString().toMoneyString().plus(" ₽"),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h3
        )
        
    }
}












