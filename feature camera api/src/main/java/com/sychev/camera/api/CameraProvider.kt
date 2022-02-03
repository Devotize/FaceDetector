package com.sychev.camera.api

import androidx.compose.runtime.compositionLocalOf

/*
Contains all dependencies camera provides
 */
interface CameraProvider

val LocalCameraProvider = compositionLocalOf { error("No camera provider found") }