package com.sychev.facedetector.presentation.ui.components

import android.graphics.RectF
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.utils.TAG

@ExperimentalMaterialApi
@Composable
fun ClothesPointer(
    location: RectF,
    onPointerClick: () -> Unit,
    loading: Boolean
) {
    val x = with(LocalDensity.current) {location.centerX().toDp()}
    val y = with(LocalDensity.current) {location.centerY().toDp()}
    val alphaAnim = rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(
        modifier = Modifier
            .size(23.dp)
            .offset(x = x, y = y)
            .graphicsLayer {
                if (loading) {
                    alpha = alphaAnim.value
                }
            },
        backgroundColor = Color.Red,
        shape = CircleShape,
        border = BorderStroke(6.dp, Color.White),
        onClick = {
            onPointerClick()
                  },
    ){

    }
}