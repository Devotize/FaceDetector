package com.sychev.facedetector.presentation.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val lightThemeColors = lightColors(
    primary = White,
    primaryVariant = Grey4,
    secondary = Blue1,
    background = Grey1,
    surface = White,
    error = Red1,
    onPrimary = Grey3,
    onSecondary  = Grey3,
    onSurface = Grey3,
    onBackground = Grey2
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = lightThemeColors,
        typography = RubikTypography,
        shapes = AppShapes
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ){
            content()
        }
    }
}