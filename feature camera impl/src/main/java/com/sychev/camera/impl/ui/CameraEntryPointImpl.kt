package com.sychev.camera.impl.ui

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.sychev.camera.api.CameraEntryPoint
import com.sychev.camera.impl.di.CameraComponent
import com.sychev.camera.impl.di.DaggerCameraComponent
import com.sychev.camera.impl.ui.components.CameraPreview
import com.sychev.common.Destinations
import com.sychev.common.PermissionManager
import com.sychev.common.di.DaggerCommonComponent
import com.sychev.common.di.injectedViewModel
import com.sychev.feature.define.gender.impl.di.DaggerDefineGenderComponent
import com.sychev.feature.preferences.api.LocalPreferencesProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class CameraEntryPointImpl @Inject constructor(
    private val permissionManager: PermissionManager,
): CameraEntryPoint() {

    @Composable
    override fun NavGraphBuilder.Composable(
        navController: NavHostController,
        destinations: Destinations,
        backStackEntry: NavBackStackEntry,
    ) {
        val context = LocalContext.current
        val cameraComponent = initCameraComponent(context)
        val viewModel = injectedViewModel {
            cameraComponent.viewModel
        }
        val previewView = PreviewView(context)
        val scope = rememberCoroutineScope()
        val shouldShowCamera = viewModel.shouldShowCamera.collectAsState(initial = null).value
        val lifecycleOwner = LocalLifecycleOwner.current
        shouldShowCamera?.let {
            if (!shouldShowCamera) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.onSurface)
                )
                permissionManager.askForCameraPermission() { isGranted ->
                    if (!isGranted) {
                        navController.popBackStack()
                    } else {
                        viewModel.setCameraPermission(isGranted)
                    }
                }
            } else {
                Content(navController = navController, previewView)

                previewView.previewStreamState.observe(lifecycleOwner) {
                    if (it == PreviewView.StreamState.STREAMING) {
                        scope.launch {
                            viewModel.needStartJob.emit(Unit)
                        }
                    }
                }
                scope.launch {
                    viewModel.needStartJob.collect {
                        previewView.bitmap?.let {
                            viewModel.startProcessingJob(bitmap = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun initCameraComponent(context: Context): CameraComponent {
    val commonComponent = DaggerCommonComponent.factory().create(context)
    val defineGenderComponent =
        DaggerDefineGenderComponent.builder().commonProvider(commonComponent).build()
    return DaggerCameraComponent.builder()
        .preferencesProvider(LocalPreferencesProvider.current)
        .defineGenderComponent(defineGenderComponent)
        .build()
}

@Composable
private fun Content(
    navController: NavHostController,
    previewView: PreviewView,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CameraPreview(
            modifier = Modifier
                .fillMaxSize(),
            previewView,
        )
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                modifier = Modifier
                    .padding(8.dp),
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}