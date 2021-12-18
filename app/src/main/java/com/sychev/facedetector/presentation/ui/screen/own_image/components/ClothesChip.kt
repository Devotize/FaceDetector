package com.sychev.facedetector.presentation.ui.screen.own_image.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes

@Composable
fun ClothesChip(
    clothes: Clothes,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(60.dp)
            .padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 4.dp,
                end = 4.dp
            )
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colors.secondary) else null,
    ) {
        val imagePainter = rememberImagePainter(data = clothes.picUrl){
            crossfade(true)
            error(R.drawable.clothes_default_icon_gray)
        }

            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                painter = imagePainter,
                contentDescription = null,
            )

    }
}