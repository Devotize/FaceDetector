package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.SavedScreenshot

@Composable
fun ScreenshotItem(
    savedScreenshot: SavedScreenshot
){
    Card(
        modifier = Modifier
            .padding(2.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(4)
    ) {

        Image(
            modifier = Modifier
                .width(200.dp)
                .height(250.dp),
            bitmap = savedScreenshot.image.asImageBitmap(),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )

    }
}