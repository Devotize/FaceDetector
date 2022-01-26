package com.sychev.facedetector.presentation.ui.screen.camera

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.sychev.facedetector.interactors.clothes_list.DetectClothesLocal
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.ui.screen.own_image.WrappedClothes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val detectClothesLocal: DetectClothesLocal,
    private val searchClothes: SearchClothes,
): ViewModel() {
    private val clothes = mutableListOf<WrappedClothes>()



}