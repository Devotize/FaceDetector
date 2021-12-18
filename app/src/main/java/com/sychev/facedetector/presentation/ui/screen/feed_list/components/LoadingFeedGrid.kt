package com.sychev.facedetector.presentation.ui.screen.feed_list.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import com.sychev.facedetector.presentation.ui.components.StaggeredVerticalGrid

@Composable
fun LoadingFeedGrid(
    maxColumnWidth: Dp,
    maxScreenHeight: Dp,
) {
    StaggeredVerticalGrid(
        modifier = Modifier.padding(4.dp),
        maxColumnWidth = maxColumnWidth,
    ) {
        repeat(10) {
            val density = LocalDensity.current
            val maxHeightPx = with(density){maxScreenHeight.toPx().toInt()}
            val heightPx = ((maxHeightPx / 3.5f).toInt()..(maxHeightPx / 2.2f).toInt()).random()
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .width(maxColumnWidth)
                    .height(with(density){heightPx.toDp()}),
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium,
                elevation = 0.dp
            ) {

            }
        }
    }
}