package com.sychev.common

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import javax.inject.Inject

class PermissionManager @Inject constructor() {

    @SuppressLint("ComposableNaming")
    @Composable
    fun askForCameraPermission(onResultAction: (Boolean) -> Unit = {}) {
        //there is a bug when onResult callback called before actual user click
        var onResultCallbackCount = 0
        val request = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = {
                val needDropFirstResult = onResultCallbackCount < 1
                if (!needDropFirstResult) {
                    onResultAction.invoke(it)
                }
                onResultCallbackCount++
            }
        )
        SideEffect {
            request.launch(Manifest.permission.CAMERA)
        }
    }
}