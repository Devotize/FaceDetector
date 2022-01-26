package com.sychev.facedetector.presentation.ui.screen.own_image.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChangeCurrentImageButton(
    modifier: Modifier = Modifier,
    isNext: Boolean,
    onClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = MaterialTheme.colors.primary.copy(alpha = .6f),
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                imageVector = if (isNext) Icons.Default.NavigateNext else Icons.Default.NavigateBefore,
                contentDescription = null,
                tint = Color.Black,
            )
        }
    }
}