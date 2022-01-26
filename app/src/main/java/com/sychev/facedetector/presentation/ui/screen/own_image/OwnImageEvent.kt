package com.sychev.facedetector.presentation.ui.screen.own_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.ImageData
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailEvent

sealed class OwnImageEvent {
    class CreateImageUri(val context: Context, val createdUri: (Uri) -> Unit,): OwnImageEvent()
    class DetectClothesLocal(val context: Context, val croppedBitmap: Bitmap, val wrappedImageData: WrappedImageData,): OwnImageEvent()
    class SearchClothes(val context: Context, val detectedClothes: DetectedClothes, val wrappedImageData: WrappedImageData): OwnImageEvent()
    class OnGenderChange(val newGender: String, val imageData: WrappedImageData, val clothes: WrappedClothes, val context: Context): OwnImageEvent()
    class InsertBitmapInCache(val bitmap: Bitmap, val context: Context): OwnImageEvent()
    object GoToRetailScreen: OwnImageEvent()
    class GetImagesFromCache(val context: Context, val hasImages: (Boolean) -> Unit): OwnImageEvent()
}