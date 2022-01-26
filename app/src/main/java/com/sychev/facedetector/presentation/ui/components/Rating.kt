package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Rating(
    modifier: Modifier = Modifier,
    rating: Double,
    starSize: Dp = 14.dp,
    textStyle: TextStyle = MaterialTheme.typography.caption
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colors.onPrimary,
    ) {
        Row(
            modifier = Modifier.padding(top = 1.dp, bottom = 1.dp, start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(starSize),
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.Yellow
            )
            Text(
                text = "$rating",
                color = MaterialTheme.colors.primary,
                style = textStyle,
            )
        }
    }

}
