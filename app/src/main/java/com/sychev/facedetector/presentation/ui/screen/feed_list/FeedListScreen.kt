package com.sychev.facedetector.presentation.ui.screen.feed_list

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.*
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.presentation.activity.ClothesRetailActivity
import com.sychev.facedetector.presentation.ui.components.ClothesPointer
import com.sychev.facedetector.presentation.ui.components.FoundedClothesCard
import com.sychev.facedetector.utils.TAG
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
    val pictures = viewModel.pictures
    val scrollState = rememberLazyListState()
    val detectedClothes = viewModel.detectedClothes
    val foundedClothesList = viewModel.foundedClothes
    val scope = rememberCoroutineScope()
    val filteredPictures = pictures.filterIndexed { index, bitmap -> index % 2 != 0 }

    if (scrollState.firstVisibleItemIndex == filteredPictures.lastIndex - 1) {
        viewModel.onTriggerEvent(FeedEvent.GetCelebPicsEvent())
    }
    var isScrollDelayPassed by remember { mutableStateOf(false) }

    var onStopScrollTimer: Timer? = null
    if (!scrollState.isScrollInProgress) {
        isScrollDelayPassed = false
        onStopScrollTimer = Timer()
        onStopScrollTimer.schedule(
            object : TimerTask() {
                override fun run() {
                    isScrollDelayPassed = true
                }
            },
            2500,
        )
    } else {
        onStopScrollTimer?.cancel()
        onStopScrollTimer = null
        isScrollDelayPassed = false
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (pictures.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = scrollState,
            ) {
                itemsIndexed(filteredPictures) { index: Int, item: Bitmap ->
                    var isShown by remember { mutableStateOf(false) }
                    var processing by remember { mutableStateOf(false) }
                    var scale by remember { mutableStateOf(1f) }
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf(0f) }
                    val transformableState =
                        rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                            scale *= zoomChange
                            if (scale < 1f) scale = 1f
                        }
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .transformable(transformableState)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    }
                                )
                                if (scale > 1f) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consumeAllChanges()
                                        Log.d(TAG, "FeedListScreen: dragAmountx = ${dragAmount.x}")
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                }
                            }
                    ) {
                        var imageWidthPx by remember { mutableStateOf(.0f) }
                        var imageHeightPx by remember { mutableStateOf(.0f) }
//                    Log.d(TAG, "FeedListScreen: detectedClothesList ${detectedClothesList.value}")
                        val imageLoader = ImageLoader(LocalContext.current)
                        val request = ImageRequest.Builder(LocalContext.current)
                            .data(item)
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
                        isShown = true
                        if (
                            isScrollDelayPassed && !viewModel.processedPages.contains(index)
                        ) {
                            if (scrollState.firstVisibleItemIndex == index || scrollState.firstVisibleItemIndex + 1 == index) {
                                scope.launch {
                                    resizedBitmapState.value?.let { resizedBitmap ->
                                        viewModel.onTriggerEvent(
                                            FeedEvent.DetectClothesEvent(
                                                context = context,
                                                bitmap = resizedBitmap,
                                                page = index,
                                                onLoaded = { loaded ->
                                                    processing = loaded
                                                },
                                            )
                                        )
                                    }
                                    isShown = false
                                    isScrollDelayPassed = false
                                }
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
                            var detectedClothesForCurrentIndex = mutableStateListOf<DetectedClothes>()
                            detectedClothes.forEach { pair ->
                                if (pair.first == index) {
                                    detectedClothesForCurrentIndex.clear()
                                    detectedClothesForCurrentIndex.addAll(pair.second)
                                    pair.second.forEach { item ->
                                        var isSearching by remember { mutableStateOf(false) }

                                        ClothesPointer(
                                            location = item.location,
                                            onPointerClick = {
                                                viewModel.onTriggerEvent(FeedEvent.FindClothes(
                                                    detectedClothes = item,
                                                    context = context,
                                                    page = index,
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

                            val clothesList = ArrayList<Clothes>()
                            val clothesToRemoveList = ArrayList<FeedViewModel.FoundedClothes>()
                            foundedClothesList.forEach { foundedClothes ->
                                if (foundedClothes.page == index) {
                                    clothesToRemoveList.add(foundedClothes)
                                    clothesList.addAll(foundedClothes.clothes)
                                }
                            }
                            foundedClothesList.forEach { foundedClothes ->
                                if (foundedClothes.page == index) {
                                    FoundedClothesCard(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .background(Color.Transparent)
                                            .padding(bottom = 6.dp)
                                            .fillMaxWidth(),
                                        foundedClothes = foundedClothes,
                                        onClick = {
                                            try { //trying to detect clothes for each pointer for current image
                                                viewModel.onTriggerEvent(
                                                    FeedEvent.GoToRetailScreen(
                                                        detectedClothesForCurrentIndex
                                                    )
                                                )
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        },
                                        onCloseClick = {
                                            viewModel.removeFromFoundedClothes(clothesToRemoveList)
                                        },
                                        onGenderChange = { newGender ->
                                            detectedClothes.forEach { pair ->
                                                if (pair.first == index) {
                                                    pair.second.forEach { dc ->
                                                        if (dc.location == foundedClothes.location) {
                                                            dc.gender = newGender
                                                            viewModel.onTriggerEvent(FeedEvent.FindClothes(
                                                                detectedClothes = dc,
                                                                context = context,
                                                                page = index,
                                                                location = dc.location,
                                                                onLoaded = {
                                                                    it?.let {
                                                                        if (!it) {
                                                                            viewModel.removeFromFoundedClothes(
                                                                                clothesToRemoveList
                                                                            )
                                                                        }
                                                                    }
                                                                    if (it == null) {
                                                                        viewModel.addToFoundedClothes(
                                                                            clothesToRemoveList
                                                                        )
                                                                    }
                                                                }
                                                            ))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }

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
}