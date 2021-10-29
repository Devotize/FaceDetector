package com.sychev.facedetector.presentation.ui.screen.feed_list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes

sealed class FeedEvent() {
    class GetRandomPicsEvent(
        val accessKey: String,
        val query: String,
        val count: Int
    ): FeedEvent()
    class DetectClothesEvent(
        val context: Context,
        val bitmap: Bitmap,
        val page: Int,
        val onLoaded: (Boolean) -> Unit,
        ): FeedEvent()
    class FindClothes(
        val detectedClothes: DetectedClothes,
        val context: Context,
        val page: Int,
        val location: RectF,
        val onLoaded: (Boolean?) -> Unit,
        ): FeedEvent()
    class FindMultiplyClothes(
        val detectedClothesList: List<DetectedClothes>,
        val context: Context,
        val page: Int,
        val location: RectF,
        val onLoaded: (Boolean) -> Unit,
    ): FeedEvent()
    class GetCelebPicsEvent(): FeedEvent()
    class GoToRetailScreen(val clothesList: List<DetectedClothes>): FeedEvent()
}
