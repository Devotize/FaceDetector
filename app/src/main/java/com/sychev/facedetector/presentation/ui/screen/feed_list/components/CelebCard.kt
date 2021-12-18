package com.sychev.facedetector.presentation.ui.screen.feed_list.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun CelebCard(
    modifier: Modifier = Modifier,
    imageHeight: Dp,
    image: Bitmap,
    maxHeight: Dp,
    onClick: (Bitmap) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.primary,
        elevation = 0.dp,
        shape = MaterialTheme.shapes.large
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(imageHeight)
                .fillMaxWidth(),
        ) {
            val imageWidthPx =
                with(LocalDensity.current) { this@BoxWithConstraints.maxWidth.toPx() }
            val imageHeightPx =
                with(LocalDensity.current) { this@BoxWithConstraints.maxHeight.toPx() }
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        try {
                            if (imageHeightPx > 0 && imageWidthPx > 0) {
                                val resizedBitmap = Bitmap.createBitmap(
                                    image,
                                    0,
                                    0,
                                    imageWidthPx.toInt(),
                                    imageHeightPx.toInt(),
                                )
                                onClick(resizedBitmap)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                bitmap = image.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )


        }
    }
}