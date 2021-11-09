package com.sychev.facedetector.presentation.ui.detectorAssitant

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.view.View
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes

sealed class DetectorEvent {

    class SearchClothesEvent(val detectedClothes: DetectedClothes, val context: Context, val circle: View) : DetectorEvent()

    class InsertClothesToFavoriteEvent(val clothes: Clothes): DetectorEvent()

    class DeleteClothesEvent(val clothes: Clothes): DetectorEvent()

    object GetFavoriteClothesEvent: DetectorEvent()

    object GetAllClothes: DetectorEvent()

    class GetNumClothes(val numOfElements: Int): DetectorEvent()

    class ShareMultiplyUrls(val urls: ArrayList<String>): DetectorEvent()

    class DetectClothesLocalEvent(val screenshot: Bitmap): DetectorEvent()

    class DefineGenderEvent(val screenshot: Bitmap): DetectorEvent()

    class InsertDetectedClothesEvent(val detectedClothes: List<DetectedClothes>): DetectorEvent()

    class ChangeGenderForDetectedClothes(val newGender: String, val location: RectF, val circle: View, val context: Context): DetectorEvent()

}