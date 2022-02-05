package com.sychev.facedetector.presentation.ui.screen.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sychev.camera.impl.ui.components.CameraPreview
import com.sychev.facedetector.utils.TAG

@Composable
fun CameraScree(
    viewModel: CameraScreenViewModel
) {
    var btm by remember{mutableStateOf<Bitmap?>(null)}
    val scope = rememberCoroutineScope()

    var showTest by remember{mutableStateOf(false)}
    val context = LocalContext.current
    val previewView = PreviewView(context).apply {
        this.scaleType = scaleType
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        // Preview is incorrectly scaled in Compose on some devices without this
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    }
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        SideEffect {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.primary)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp, bottom = 0.dp),
            text = "Коснитесь, чтобы посмотреть товары и узнать стоимость",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onPrimary,
        )

        Surface(
            modifier = Modifier
                .fillMaxHeight(.85f)
                .fillMaxWidth()
                .padding(top = 4.dp, start = 6.dp, end = 6.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                previewView = previewView
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                    val bitmap = previewView.bitmap
                    Log.d(TAG, "CameraScree: btm -> $bitmap")
            },
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            )
        ) {
            Text(
                text = "Подобрать одежду",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h3
            )
        }

    }

}