package com.sychev.camera.impl.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    previewView: PreviewView,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider = remember { cameraProviderFuture.get() }
    val cameraListener = remember {
        Runnable {
            bindPreview(
                lifecycleOwner,
                previewView,
                cameraProvider
            )
        }
    }
    AndroidView(
        modifier = modifier,
        factory = {
            previewView
        }) {
        cameraProviderFuture.addListener(cameraListener, ContextCompat.getMainExecutor(context))
    }
}

fun bindPreview(
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
) {
    val preview: Preview = Preview.Builder()
        .build()

    val cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    preview.setSurfaceProvider(previewView.surfaceProvider)
    cameraProvider.unbindAll()
    var camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
}