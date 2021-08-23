package com.sychev.facedetector.presentation.ui.screen.clothes_detail

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.sychev.facedetector.domain.Clothes

@Composable
fun ClothesDetailScreen(
    clothes: Clothes
){
    Text(text = clothes.itemCategory)
}