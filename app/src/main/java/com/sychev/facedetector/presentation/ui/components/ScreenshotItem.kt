package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
        modifier = Modifier.wrapContentSize()
            .padding(8.dp),
        elevation = 16.dp,
        shape = RoundedCornerShape(16)
    ) {

        Image(
            modifier = Modifier
                .width(200.dp)
                .height(300.dp),
            bitmap = savedScreenshot.image.asImageBitmap(),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )

    }
}