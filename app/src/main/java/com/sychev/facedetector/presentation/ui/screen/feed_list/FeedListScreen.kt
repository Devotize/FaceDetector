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
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
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
import com.sychev.facedetector.presentation.ui.screen.feed_list.components.FoundedClothesCardExtended
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
    var screenHeight by remember { mutableStateOf(0) }
    val isLoadCelebsCalled = viewModel.isLoadCelebsCalled.value
    val foundedClothesExtendedToDisplay = viewModel.foundedClothesExtendedToDisplay.value

    BoxWithConstraints(
        modifier = Modifier
            .onGloballyPositioned {
                screenHeight = it.size.height
            }
            .fillMaxSize(),
    ) {
        val imageHeightSeed =
            ((this@BoxWithConstraints.maxHeight.value / 5f).toInt()..(this@BoxWithConstraints.maxHeight.value / 2.1f).toInt())
        if (!isLoadCelebsCalled) {
            viewModel.onTriggerEvent(FeedEvent.GetCelebPicsEvent(imageHeightSeed))
        }
        Column {
            LazyColumn(
                state = scrollState,
                flingBehavior = StockFlingBehaviours.presetTwo(),
            ) {
                item {
                    StaggeredVerticalGrid(
                        maxColumnWidth = (this@BoxWithConstraints.maxWidth / 2),
                        modifier = Modifier
                            .onGloballyPositioned {
//                                Log.d(TAG, "FeedListScreen: ${it.size.height}")
                            }
                            .padding(4.dp),
                    ) {
//                        Log.d(TAG, "FeedListScreen: scrollState: ${scrollState.firstVisibleItemScrollOffset + screenHeight}")
                        val passedIndexes = remember { mutableStateListOf<Int>() }
                        celebImages.forEachIndexed { index, celebImage ->

                            Box(
                                modifier = Modifier.onGloballyPositioned {
                                    val rect = it.boundsInRoot()
                                    if (rect.topLeft.x != 0f || rect.topLeft.y != 0f) {
                                        if (!passedIndexes.contains(index)) {
                                            viewModel.onScrollPositionChanged(index)
                                            passedIndexes.add(index)

                                        }
                                        //getting new page
                                        if (index == celebImages.size - 2) {
                                            if (!loading) {
                                                Log.d(
                                                    TAG,
                                                    "FeedListScreen: triggering getCelebPicsEvent"
                                                )
                                                viewModel.onTriggerEvent(
                                                    FeedEvent.GetCelebPicsEvent(
                                                        imageHeightSeed
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            ) {
//                                val imageHeight = if (index % 2 == 0) this@BoxWithConstraints.maxWidth.value / 1.5f else  this@BoxWithConstraints.maxWidth.value / 1.8f
                                var bitmapHeight by remember { mutableStateOf(0) }
                                var bitmapWidth by remember { mutableStateOf(0) }
                                CelebCard(
                                    modifier = Modifier
                                        .onGloballyPositioned {
                                            bitmapHeight = it.size.height
                                            bitmapWidth = it.size.width
                                        }
                                        .padding(4.dp),
                                    image = celebImage.image,
                                    imageHeight = celebImage.height.dp,
                                    maxHeight = this@BoxWithConstraints.maxHeight,
                                    onClick = {
                                        if (!celebImage.isProcessed) {
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
                                    }
                                )
                                scrollState.isScrollInProgress.also {
                                    var timer = Timer()
                                    val timerTask = object : TimerTask() {
                                        override fun run() {
                                            if (viewModel.lastVisibleIndex - 2 == index && !celebImage.isProcessed) {
                                                Log.d(TAG, "run: timerTask triggered")
                                                val resizedBitmap = Bitmap.createScaledBitmap(
                                                    celebImage.image,
                                                    bitmapWidth,
                                                    bitmapHeight,
                                                    false
                                                )
                                                    viewModel.onTriggerEvent(
                                                        FeedEvent.DetectClothesEvent(
                                                            context = context,
                                                            resizedBitmap = resizedBitmap,
                                                            celebImage = celebImage,
                                                            onLoaded = {

                                                            }
                                                        )
                                                    )

                                            }
                                        }
                                    }
                                    if (it) {
                                        Log.d(TAG, "FeedListScreen: scrollInProgress")
                                        timer.cancel()
                                        timer.purge()
                                    } else {
                                        timer.schedule(timerTask, 1000)
                                    }
                                }
                                celebImage.foundedClothes.forEach {
                                    Log.d(TAG, "FeedListScreen: fouded clothes: $it")
                                    FoundedClothesCardSmall(
                                        location = it.location,
                                        clothes = it.clothes[0],
                                        onClick = {
                                            val fce = FoundedClothesExtended(
                                                it.location,
                                                it.clothes,
                                                celebImage.detectedClothes,
                                            )
                                            viewModel.onTriggerEvent(
                                                FeedEvent.FoundedClothesToDisplayChange(
                                                    newFoundedClothes = fce,
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        foundedClothesExtendedToDisplay?.let { fc ->
            FoundedClothesCardExtended(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp),
                foundedClothes = fc,
                onClick = {
                    viewModel.onTriggerEvent(
                        FeedEvent.GoToRetailScreen(
                            fc.detectedClothes
                        )
                    )
                },
                onCloseClick = {
                    viewModel.onTriggerEvent(FeedEvent.FoundedClothesToDisplayChange())
                },
                onGenderChange = { gender ->
                    fc.detectedClothes.forEach {
                        if (it.location == fc.location) {
                            it.gender = gender
                            viewModel.onTriggerEvent(FeedEvent.FindClothesForChangedGenderFoundedClothesExtended(
                                detectedClothes =it,
                                context = context,
                                foundedClothesExtended = fc
                            ))
                        }
                    }

                }
            )
        }
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = MaterialTheme.colors.secondary
            )
        }
    }

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
        modifier = modifier,
        color = MaterialTheme.colors.background,
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(imageHeight)
                .fillMaxWidth(),
        ) {
            val imageWidthPx =
                with(LocalDensity.current) { this@BoxWithConstraints.maxWidth.toPx() }
            val imageHeightPx =
                with(LocalDensity.current) { this@BoxWithConstraints.maxHeight.toPx() }
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
                    },
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