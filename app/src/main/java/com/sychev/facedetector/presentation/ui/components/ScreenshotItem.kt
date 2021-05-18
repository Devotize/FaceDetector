package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.SavedScreenshot


@Composable
fun ScreenshotItem(
    savedScreenshot: SavedScreenshot,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier
            .padding(2.dp)
            .width(200.dp)
            .height(250.dp)
            .clickable {
                onClick()
            },
        elevation = 8.dp,
        shape = RoundedCornerShape(4)
    ) {

        Image(
            modifier = Modifier
                .fillMaxSize(),
            bitmap = savedScreenshot.image.asImageBitmap(),
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = savedScreenshot.celebName,
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
                    .wrapContentSize()
            )
        }

        
    }
}