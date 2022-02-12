@file:SuppressLint("ComposableNaming")

package com.sychev.camera.impl.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.sychev.camera.api.CameraEntryPoint
import com.sychev.camera.impl.di.CameraComponent
import com.sychev.camera.impl.di.DaggerCameraComponent
import com.sychev.camera.impl.ui.components.CameraPreview
import com.sychev.camera.impl.viewmodel.CameraViewModel
import com.sychev.common.Destinations
import com.sychev.common.PermissionManager
import com.sychev.common.di.DaggerCommonComponent
import com.sychev.common.di.injectedViewModel
import com.sychev.feature.define.clothes.impl.di.DaggerDefineClothesComponent
import com.sychev.feature.define.gender.impl.di.DaggerDefineGenderComponent
import com.sychev.feature.preferences.api.LocalPreferencesProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class CameraEntryPointImpl @Inject constructor(
    private val permissionManager: PermissionManager,
): CameraEntryPoint() {

    private lateinit var previewView: PreviewView

    override fun onInit(context: Context) {
        super.onInit(context)
        previewView =
            PreviewView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
    }

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
                DetectedBoxes(viewModel = viewModel)
                SideEffect {
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
}

@Composable
private fun initCameraComponent(context: Context): CameraComponent {
    val commonComponent = DaggerCommonComponent.factory().create(context)
    val defineGenderComponent =
        DaggerDefineGenderComponent.builder().commonProvider(commonComponent).build()
    val defineClothesComponent =
        DaggerDefineClothesComponent.builder().commonProvider(commonComponent).build()
    return DaggerCameraComponent.builder()
        .preferencesProvider(LocalPreferencesProvider.current)
        .defineGenderComponent(defineGenderComponent)
        .defineClothesComponent(defineClothesComponent)
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

@Composable
private fun DetectedBox(rectF: RectF) {
    val heightDp = with(LocalDensity.current) { rectF.height().toDp() }
    val widthDp = with(LocalDensity.current) { rectF.width().toDp() }
    val paddingStartDp = with(LocalDensity.current) { rectF.left.toDp() }
    val paddingTopDp = with(LocalDensity.current) { rectF.top.toDp() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = rectF.left
                translationY = rectF.top
            }
    ) {
        Box(
            modifier = Modifier
                .height(heightDp)
                .width(widthDp)
                .border(1.dp, Color.Blue),
        )
    }
}

@Composable
private fun DetectedBoxes(viewModel: CameraViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        val detectedClothesWithGender =
            viewModel.detectedClothesWithGender.collectAsState(initial = null).value
        detectedClothesWithGender?.let { detectedClothesWithGender ->
            detectedClothesWithGender.detectedClothes.list.forEach {
                DetectedBox(rectF = it.location)
            }
        }
    }
}