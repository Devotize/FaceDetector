package com.sychev.camera.impl.ui

import CameraPreview
import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.sychev.camera.api.CameraEntryPoint
import com.sychev.camera.impl.di.CameraComponent
import com.sychev.camera.impl.di.DaggerCameraComponent
import com.sychev.common.Destinations
import com.sychev.common.PermissionManager
import com.sychev.common.di.DaggerCommonComponent
import com.sychev.common.di.injectedViewModel
import com.sychev.feature.define.gender.impl.di.DaggerDefineGenderComponent
import com.sychev.feature.preferences.api.LocalPreferencesProvider
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
//        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.onSurface))
        val previewView = PreviewView(context)

        CameraPreview(
            modifier = Modifier.fillMaxSize(),
        )

//            val shouldShowCamera = viewModel.shouldShowCamera.collectAsState(initial = null).value
//            shouldShowCamera?.let{
//                if (!shouldShowCamera) {
//                    permissionManager.askForCameraPermission() { isGranted ->
//                        if (!isGranted) {
//                            navController.popBackStack()
//                        } else {
//                            viewModel.setCameraPermission(isGranted)
//                        }
//                    }
//                } else {
//                    Content(context = context, navController = navController)
//                }
//            }
    }
}

@Composable
fun initCameraComponent(context: Context): CameraComponent {
    val commonComponent = DaggerCommonComponent.factory().create(context)
    val defineGenderComponent = DaggerDefineGenderComponent.builder().commonProvider(commonComponent).build()
    return DaggerCameraComponent.builder()
        .preferencesProvider(LocalPreferencesProvider.current)
        .defineGenderComponent(defineGenderComponent)
        .build()
}

@Composable
private fun Content(
    context: Context,
    navController: NavHostController,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val previewView = PreviewView(context)
        CameraPreview(
            modifier = Modifier
                .border(2.dp, Color.Red)
                .fillMaxSize(),
        )
//        Box(modifier = Modifier.fillMaxSize()) {
//            IconButton(
//                modifier = Modifier
//                    .padding(8.dp),
//                onClick = {
//                    navController.popBackStack()
//                },
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBackIos,
//                    contentDescription = null,
//                    tint = MaterialTheme.colors.primary
//                )
//            }
//        }
    }
}