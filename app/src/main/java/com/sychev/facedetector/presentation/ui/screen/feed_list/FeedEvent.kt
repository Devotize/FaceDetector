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
        val resizedBitmap: Bitmap,
        val celebImage: CelebImage,
        val onLoaded: (Boolean) -> Unit,
        ): FeedEvent()
    class FindClothes(
        val celebImage: CelebImage,
        val detectedClothes: DetectedClothes,
        val context: Context,
        val location: RectF,
        val onLoaded: (Boolean?) -> Unit,
        ): FeedEvent()
    class FindMultiplyClothes(
        val celebImage: CelebImage,
        val detectedClothesList: List<DetectedClothes>,
        val context: Context,
        val page: Int,
        val location: RectF,
        val onLoaded: (Boolean) -> Unit,
    ): FeedEvent()
    class GetCelebPicsEvent(val seed: IntRange, val context: Context): FeedEvent()
    class GoToRetailScreen(val clothesList: List<DetectedClothes>): FeedEvent()
    class FoundedClothesToDisplayChange(val newFoundedClothes: FoundedClothesExtended? = null): FeedEvent()
    class FindClothesForChangedGenderFoundedClothesExtended(
        val detectedClothes: DetectedClothes,
        val context: Context,
        val foundedClothesExtended: FoundedClothesExtended
        ): FeedEvent()
}
