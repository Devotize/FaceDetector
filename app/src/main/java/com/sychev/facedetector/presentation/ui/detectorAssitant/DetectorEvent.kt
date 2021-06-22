package com.sychev.facedetector.presentation.ui.detectorAssitant

import android.graphics.Bitmap
import com.sychev.facedetector.domain.DetectedClothes

sealed class DetectorEvent {

    class SearchClothesEvent(val screenshot: Bitmap) : DetectorEvent()

    class InsertClothesToFavoriteEvent(val detectedClothes: DetectedClothes): DetectorEvent()

    class DeleteDetectedClothesEvent(val detectedClothes: DetectedClothes): DetectorEvent()

    object GetFavoriteClothesEvent: DetectorEvent()

    object GetAllDetectedClothes: DetectorEvent()

    class GetNumDetectedClothes(val numOfElements: Int): DetectorEvent()

}