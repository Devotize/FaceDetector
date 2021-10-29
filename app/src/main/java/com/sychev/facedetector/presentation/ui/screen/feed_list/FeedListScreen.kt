package com.sychev.facedetector.presentation.ui.screen.feed_list

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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
import com.sychev.facedetector.presentation.ui.components.StaggeredVerticalGrid
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
    val celebImages = viewModel.celebImages
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

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
        if (celebImages.isNotEmpty()) {
            StaggeredVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                maxColumnWidth = 2.dp,
            ) {

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