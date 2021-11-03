package com.sychev.facedetector.presentation.ui.screen.feed_list

import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.*
import com.google.android.material.chip.Chip
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.presentation.activity.ClothesRetailActivity
import com.sychev.facedetector.presentation.ui.components.ClothesPointer
import com.sychev.facedetector.presentation.ui.components.FoundedClothesCard
import com.sychev.facedetector.presentation.ui.components.FoundedClothesCardSmall
import com.sychev.facedetector.presentation.ui.components.StaggeredVerticalGrid
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.onShown
import com.sychev.facedetector.utils.random
import com.sychev.facedetector.utils.toMoneyString
import io.iamjosephmj.flinger.bahaviours.StockFlingBehaviours
import io.iamjosephmj.flinger.flings.FlingerFlingBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random


@OptIn(ExperimentalMaterialApi::class)
@ExperimentalCoilApi
@ExperimentalPagerApi
@Composable
fun FeedListScreen(
    viewModel: FeedViewModel,
) {
    val context = LocalContext.current
    val loading = viewModel.loading.value
    val urls = viewModel.urls
    val celebImages = viewModel.celebImages.value
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Column {
            LazyColumn(
//                state = scrollState,
                flingBehavior = StockFlingBehaviours.presetTwo(),
            ) {
                item {
                    StaggeredVerticalGrid(
                        maxColumnWidth = (this@BoxWithConstraints.maxWidth / 2),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        celebImages.forEachIndexed {index, celebImage ->
                            Box() {
                                val imageHeight = if (index % 2 == 0) this@BoxWithConstraints.maxWidth.value / 1.6f else  this@BoxWithConstraints.maxWidth.value / 2f
                                CelebCard(
                                    image = celebImage.image,
                                    imageHeight = imageHeight.dp,
                                    maxHeight = this@BoxWithConstraints.maxHeight,
                                    onClick = {
                                        viewModel.onTriggerEvent(
                                            FeedEvent.DetectClothesEvent(
                                                context = context,
                                                resizedBitmap = it,
                                                celebImage = celebImage,
                                                onLoaded = {

                                                }
                                            )
                                        )
                                    }
                                )
                                celebImage.foundedClothes.forEach {
                                    Log.d(TAG, "FeedListScreen: fouded clothes: $it")
                                    FoundedClothesCardSmall(
                                        location = it.location,
                                        clothes = it.clothes[0],
                                        onClick = {
                                            viewModel.onTriggerEvent(
                                                FeedEvent.GoToRetailScreen(
                                                    celebImage.detectedClothes
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                        Box(modifier = Modifier
                            .onFocusChanged {
                                Log.d(TAG, "FeedListScreen: focuschanged")
                            }
                            .height(25.dp)){
                            Box(
                                modifier = Modifier.padding(6.dp)
                                    ,
                            ) {

                            }
                        }
                }
            }
        }

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = MaterialTheme.colors.secondary
            )
        }
    }
//    if (celebImages.isNotEmpty()) {
//        val visibleItems = scrollState.visibleItems(50f)
//            .map { celebImages[it.index] }
//
//        Log.d(TAG, "App: ${visibleItems.map {
//            celebImages.indexOf(it)
//        }}")
//    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomSheetCeleb(
    celebImage: CelebImage,
    viewModel: FeedViewModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var processing by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .wrapContentSize(),
    ) {
        var imageWidthPx by remember { mutableStateOf(.0f) }
        var imageHeightPx by remember { mutableStateOf(.0f) }
//                    Log.d(TAG, "FeedListScreen: detectedClothesList ${detectedClothesList.value}")
        val imageLoader = ImageLoader(LocalContext.current)
        val request = ImageRequest.Builder(LocalContext.current)
            .data(celebImage.image)
            .crossfade(true)
            .transformations(RoundedCornersTransformation(12.dp.value))
            .fallback(R.drawable.clothes_default_icon_gray)
            .build()
        val imagePainter = rememberImagePainter(
            request = request,
            imageLoader = imageLoader
        )
        val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
        val resizedBitmapState = remember { mutableStateOf<Bitmap?>(null) }
        scope.launch {
            try {
                val result =
                    (imageLoader.execute(request) as SuccessResult).drawable
                val bitmap = (result as BitmapDrawable).bitmap
                val resizedBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    imageWidthPx.toInt(),
                    imageHeightPx.toInt(),
                    false
                )
                bitmapState.value = bitmap
                resizedBitmapState.value = resizedBitmap
            } catch (e: Exception) {
                Log.d(TAG, "FeedListScreen: exception: ${e.message}")
                processing = false
                e.printStackTrace()
            }
        }
        scope.launch {
            resizedBitmapState.value?.let { resizedBitmap ->
                viewModel.onTriggerEvent(
                    FeedEvent.DetectClothesEvent(
                        context = context,
                        resizedBitmap = resizedBitmap,
                        celebImage = celebImage,
                        onLoaded = { loaded ->
                            processing = loaded
                        },
                    )
                )
            }
        }


//                    bitmapState.value?.let{bitmap ->
        BoxWithConstraints(
            modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colors.primaryVariant,
//                            shape = RoundedCornerShape(12.dp.value),
//                        )
                .fillMaxWidth()
                .padding(28.dp)
                .height(320.dp)
        ) {

            imageWidthPx = with(LocalDensity.current) { (maxWidth).toPx() }
            imageHeightPx = with(LocalDensity.current) { (maxHeight).toPx() }
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = imagePainter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            celebImage.detectedClothes.forEach { item ->
                var isSearching by remember { mutableStateOf(false) }
                ClothesPointer(
                    location = item.location,
                    onPointerClick = {
                        viewModel.onTriggerEvent(FeedEvent.FindClothes(
                            celebImage = celebImage,
                            detectedClothes = item,
                            context = context,
                            location = item.location,
                            onLoaded = {
                                if (it != null) {
                                    isSearching = it
                                }
                            }
                        ))

                    },
                    loading = isSearching
                )
            }
        }
    }
    celebImage.foundedClothes.forEach { foundedClothes ->
        FoundedClothesCard(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(bottom = 6.dp)
                .fillMaxWidth(),
            foundedClothes = foundedClothes,
            onClick = {
                try {
                    //trying to detect clothes for each pointer for current image
                    viewModel.onTriggerEvent(
                        FeedEvent.GoToRetailScreen(
                            celebImage.detectedClothes
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            onCloseClick = {
                viewModel.removeFromFoundedClothes(celebImage, foundedClothes)
            },
            onGenderChange = { newGender ->
                celebImage.detectedClothes.forEach { dc ->
                    if (dc.location == foundedClothes.location) {
                        dc.gender = newGender
                        viewModel.onTriggerEvent(FeedEvent.FindClothes(
                            celebImage = celebImage,
                            detectedClothes = dc,
                            context = context,
                            location = dc.location,
                            onLoaded = {
                                it?.let {
                                    if (!it) {
                                        viewModel.removeFromFoundedClothes(
                                            celebImage, foundedClothes
                                        )
                                    }
                                }
                                if (it == null) {
                                    viewModel.addToFoundedClothes(
                                        celebImage, foundedClothes
                                    )
                                }
                            }
                        ))
                    }
                }
            }
        )

    }

    if (processing) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize(),
        ) {
            val heightPx = with(LocalDensity.current) { (maxHeight).toPx() }
            val gradientWidth = heightPx * 0.2f
            val xShimmer by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = (maxWidth.value + gradientWidth),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        easing = LinearEasing,
                        delayMillis = 150
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
            val yShimmer by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = (heightPx + gradientWidth + 1300f),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        easing = LinearEasing,
                        delayMillis = 150
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            val shader = LinearGradientShader(
                from = Offset(
                    xShimmer - gradientWidth,
                    yShimmer - gradientWidth
                ),
                to = Offset(xShimmer, yShimmer),
                colors = listOf(
                    Color.LightGray.copy(alpha = .8f),
                    Color.LightGray.copy(alpha = .2f),
                    Color.LightGray.copy(alpha = .8f),
                ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = ShaderBrush(shader),
                        shape = RoundedCornerShape(16.dp),
                    ),
            ) {

            }
        }
    }
}


@Composable
private fun CelebCard(
    modifier: Modifier = Modifier,
    imageHeight: Dp,
    image: Bitmap,
    maxHeight: Dp,
    onClick: (Bitmap) -> Unit,
) {
    Surface(
        modifier = modifier
            .padding(4.dp),
        color = MaterialTheme.colors.background,
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        val maxCardHeight = (maxHeight.value / 2.4f).toInt()
        val minCardHeight = (maxHeight.value / 3).toInt()
        BoxWithConstraints (modifier = Modifier
            .height(imageHeight)
            .fillMaxWidth(),
        ) {
            val imageWidthPx = with(LocalDensity.current){this@BoxWithConstraints.maxWidth.toPx()}
            val imageHeightPx = with(LocalDensity.current){this@BoxWithConstraints.maxHeight.toPx()}
            val resizedBitmap = Bitmap.createScaledBitmap(
                image,
                imageWidthPx.toInt(),
                imageHeightPx.toInt(),
                false
            )
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onClick(resizedBitmap)
                    }
                ,
                bitmap = image.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}


fun LazyListState.visibleItems(itemVisiblePercentThreshold: Float) =
    layoutInfo
        .visibleItemsInfo
        .filter {
            visibilityPercent(it) >= itemVisiblePercentThreshold
        }

fun LazyListState.visibilityPercent(info: LazyListItemInfo): Float {
    val cutTop = max(0, layoutInfo.viewportStartOffset - info.offset)
    val cutBottom = max(0, info.offset + info.size - layoutInfo.viewportEndOffset)

    return max(0f, 100f - (cutTop + cutBottom) * 100f / info.size)
}