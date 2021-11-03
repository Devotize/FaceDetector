package com.sychev.facedetector.presentation.ui.screen.own_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.sychev.facedetector.domain.DetectedClothes

sealed class OwnImageEvent {
    class CreateImageUri(val context: Context, val createdUri: (Uri) -> Unit,): OwnImageEvent()
    class DetectClothesLocal(val context: Context, val croppedBitmap: Bitmap): OwnImageEvent()
    class SearchClothes(val context: Context, val detectedClothes: DetectedClothes): OwnImageEvent()
    object GoToRetailScreen: OwnImageEvent()
}