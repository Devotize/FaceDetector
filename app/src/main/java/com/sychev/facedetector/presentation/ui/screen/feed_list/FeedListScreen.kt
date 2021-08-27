package com.sychev.facedetector.presentation.ui.screen.feed_list

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
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
    val pagerState = rememberPagerState(
        pageCount = pictures.size,
        initialOffscreenLimit = 2
    )
    val detectedClothes = viewModel.detectedClothes
    val foundedClothesList = viewModel.foundedClothes
    val scope = rememberCoroutineScope()

    if (pagerState.currentPage == pictures.lastIndex) {
        viewModel.onTriggerEvent(FeedEvent.GetCelebPicsEvent())
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (pictures.isNotEmpty()) {
            VerticalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
                flingBehavior = PagerDefaults.defaultPagerFlingConfig(
                    state = pagerState,
                    decayAnimationSpec = FloatExponentialDecaySpec().generateDecayAnimationSpec(),
                    snapAnimationSpec = tween(easing = LinearOutSlowInEasing)
                ),
            ) { page: Int ->
                var processing by remember{mutableStateOf(false)}
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
                        .graphicsLayer {
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                            lerp(
                                start = 0.90f,
                                stop = 1f,
                                fraction = 1f - pageOffset
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                ) {
                    var imageWidthPx by remember{mutableStateOf(.0f)}
                    var imageHeightPx by remember{mutableStateOf(.0f)}
                    val detectedClothesList = remember { mutableStateOf<List<DetectedClothes>?>(null) }
//                    Log.d(TAG, "FeedListScreen: detectedClothesList ${detectedClothesList.value}")
                    val imageLoader = ImageLoader(LocalContext.current)
                    val request = ImageRequest.Builder(LocalContext.current)
                        .data(pictures[page])
                        .crossfade(true)
                        .transformations(RoundedCornersTransformation(12.dp.value))
                        .fallback(R.drawable.clothes_default_icon_gray)
                        .build()
                    val imagePainter = rememberImagePainter(
                        request = request,
                        imageLoader = imageLoader
                    )
                    val bitmapState = remember{mutableStateOf<Bitmap?>(null)}
                    scope.launch {
                        try {
                            val result =
                                (imageLoader.execute(request) as SuccessResult).drawable
                            val bitmap = (result as BitmapDrawable).bitmap
//                                Log.d(TAG, "FeedListScreen: bitmap: $bitmap, page: $page")
//                                Log.d(TAG, "FeedListScreen: btimapheight: ${bitmap.height}, bitmapWidth: ${bitmap.width}")
//                                Log.d(TAG, "FeedListScreen: imageWidth: ${imageWidthPx}, imageHeightPx: ${imageHeightPx}")
                            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageWidthPx.toInt(), imageHeightPx.toInt(), false)
//                                Log.d(
//                                    TAG,
//                                    "FeedListScreen: processedPages:${viewModel.processedPages.toList()}"
//                                )
//                                Log.d(TAG, "FeedListScreen: currentPage: ${pagerState.currentPage}")
                            delay(1000)
                            if (!viewModel.processedPages.contains(page) && pagerState.currentPage == page) {
                                viewModel.onTriggerEvent(FeedEvent.DetectClothesEvent(
                                    context = context,
                                    bitmap = resizedBitmap,
                                    page = page,
                                    onLoaded = { loaded ->
                                        processing= loaded
                                    },
                                ))
                            }
                            bitmapState.value = bitmap
                        } catch (e: Exception) {
                            Log.d(TAG, "FeedListScreen: exception: ${e.message}")
                            processing = false
                            e.printStackTrace()
                        }
                    }

//                    bitmapState.value?.let{bitmap ->
                    BoxWithConstraints(modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colors.primaryVariant,
//                            shape = RoundedCornerShape(12.dp.value),
//                        )
                        .fillMaxWidth()
                        .height(this@BoxWithConstraints.maxHeight - 150.dp)
                        .graphicsLayer {
                            scaleX = maxOf(1f, minOf(3f, scale))
                            scaleY = maxOf(1f, minOf(3f, scale))
                            translationX = offsetX
                            translationY = offsetY
                        }) {
                        imageWidthPx = with(LocalDensity.current) { (maxWidth).toPx() }
                        imageHeightPx = with(LocalDensity.current) { (maxHeight).toPx() }
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = maxOf(1f, minOf(3f, scale))
                                    scaleY = maxOf(1f, minOf(3f, scale))
                                    translationX = offsetX
                                    translationY = offsetY
                                },
                            painter = imagePainter,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                        detectedClothes.forEach { pair ->
                            if (pair.first == page) {
                                pair.second.forEach { item ->
                                    var isSearching by remember{mutableStateOf(false)}
                                    ClothesPointer(
                                        location = item.location,
                                        onPointerClick = {
                                            viewModel.onTriggerEvent(FeedEvent.FindClothes(
                                                detectedClothes = item,
                                                context = context,
                                                page = page,
                                                location = item.location,
                                                onLoaded = {
                                                    isSearching = it
                                                }
                                            ))
                                        },
                                        loading = isSearching
                                    )
                                }
                            }
                        }

                        val clothesList = ArrayList<Clothes>()
                        foundedClothesList.forEach { foundedClothes ->
                            clothesList.addAll(foundedClothes.clothes)
                        }
                        foundedClothesList.forEach { foundedClothes ->
                            if (foundedClothes.page == page) {
                                FoundedClothesCard(
                                    foundedClothes = foundedClothes,
                                    onClick = {
                                        try {
                                            val intent = Intent(context, ClothesRetailActivity::class.java)
                                            val selectedClothes = ArrayList<Clothes>()
                                            selectedClothes.addAll(foundedClothes.clothes)
                                            intent.putExtra("clothes_list", clothesList)
                                            intent.putExtra("selected_clothes", selectedClothes)
                                            context.startActivity(intent)
                                        }catch (e: Exception){
                                            e.printStackTrace()
                                        }
                                    },
                                    onCloseClick = {
                                        viewModel.removeFromFoundedClothes(foundedClothes)
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
                                    from = Offset(xShimmer - gradientWidth,yShimmer - gradientWidth),
                                    to = Offset(xShimmer, yShimmer),
                                    colors = listOf(
                                        Color.LightGray.copy(alpha = .8f),
                                        Color.LightGray.copy(alpha = .2f),
                                        Color.LightGray.copy(alpha = .8f),
                                    ),
                                )
                                Box(modifier = Modifier
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

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = MaterialTheme.colors.secondary
            )
        }
    }
}