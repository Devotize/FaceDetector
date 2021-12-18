package com.sychev.facedetector.presentation.ui.screen.shop.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ExpandButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val rotationFloat by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f)
    IconButton(
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = rotationFloat
                },
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colors.onPrimary
        )
    }
}