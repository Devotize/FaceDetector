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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
import com.sychev.facedetector.presentation.ui.screen.feed_list.components.CelebCard
import com.sychev.facedetector.presentation.ui.screen.feed_list.components.FoundedClothesCardExtended
import com.sychev.facedetector.presentation.ui.screen.feed_list.components.LoadingFeedGrid
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
    
    
    val refreshState = rememberSwipeRefreshState(isRefreshing = (loading && celebImages.isEmpty()))

        BoxWithConstraints(
            modifier = Modifier
                .onGloballyPositioned {
                    screenHeight = it.size.height
                }
                .fillMaxSize(),
        ) {
            val imageHeightSeed =
                ((this@BoxWithConstraints.maxHeight.value / 5f).toInt()..(this@BoxWithConstraints.maxHeight.value / 2.5f).toInt())
            SwipeRefresh(
                state = refreshState,
                onRefresh = {
                    viewModel.celebImages.value.clear()
                    viewModel.page = 0
                    viewModel.onTriggerEvent(FeedEvent.GetCelebPicsEvent(imageHeightSeed))
                    Log.d(TAG, "FeedListScreen: onRefresh: called")
                }
            ) {
            if (!isLoadCelebsCalled) {
                viewModel.onTriggerEvent(FeedEvent.GetCelebPicsEvent(imageHeightSeed))
            }
            Column {
                LazyColumn(
                    state = scrollState,
                    flingBehavior = StockFlingBehaviours.presetTwo(),
                ) {
                    if (celebImages.isEmpty() && loading) {
                        item {
                            LoadingFeedGrid(
                                maxColumnWidth = this@BoxWithConstraints.maxWidth / 2,
                                maxScreenHeight = this@BoxWithConstraints.maxHeight,
                            )
                        }
                    }
                    item {
                        StaggeredVerticalGrid(
                            maxColumnWidth = (this@BoxWithConstraints.maxWidth / 2),
                            modifier = Modifier
                                .padding(8.dp),
                        ) {
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
                                            .padding(8.dp),
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
                                                try {
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
                                                }catch (e: Exception) {
                                                    e.printStackTrace()
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
                                viewModel.onTriggerEvent(
                                    FeedEvent.FindClothesForChangedGenderFoundedClothesExtended(
                                        detectedClothes = it,
                                        context = context,
                                        foundedClothesExtended = fc
                                    )
                                )
                            }
                        }

                    }
                )
            }
            if (loading && celebImages.isNotEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center),
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}
