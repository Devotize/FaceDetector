package com.sychev.facedetector.presentation.ui.screen.own_image

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Panorama
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sychev.facedetector.presentation.ui.components.FoundedClothesCardSmall
import com.sychev.facedetector.utils.TAG


@Composable
fun OwnImageScreen(
    viewModel: OwnImageViewModel,
) {
    val context = LocalContext.current
    val imageUri = viewModel.imageUri.value
    val foundedClothes = viewModel.foundedClothes
    var imageWidthPx by remember {
        mutableStateOf(0f)
    }
    var imageHeightPx by remember {
        mutableStateOf(0f)
    }
    val isImageAlreadyProcessed = viewModel.isImageAlreadyProcessed.value

    var image by remember{mutableStateOf<Bitmap?>(null)}
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            viewModel.imageUri.value = it
            viewModel.isImageAlreadyProcessed.value = false
        }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                Log.d(TAG, "OwnImageScreen: picture activity reslut: $it")
                val oldUri = viewModel.imageUri.value
                viewModel.imageUri.value = null
                viewModel.imageUri.value = oldUri
                viewModel.isImageAlreadyProcessed.value = false
            }
        }
    )
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                viewModel.onTriggerEvent(OwnImageEvent.CreateImageUri(context) {
                    cameraLauncher.launch(it)
                })
            }
        }
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            text = "Коснитесь, чтобы посмотреть товары и узнать стоимость",
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.W100,
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(top = 8.dp, start = 24.dp, end = 24.dp),
        ) {
            imageWidthPx = with(LocalDensity.current){this@BoxWithConstraints.maxWidth.toPx()}
            imageHeightPx = with(LocalDensity.current){this@BoxWithConstraints.maxHeight.toPx()}
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.large,
                elevation = 16.dp
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
                image?.let {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }

            }
            foundedClothes.forEach {
                FoundedClothesCardSmall(
                    location = it.location,
                    clothes = it.clothes[0],
                    onClick = {
                        viewModel.onTriggerEvent(OwnImageEvent.GoToRetailScreen)
                    }
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                .align(Alignment.CenterHorizontally),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.secondary
        ) {
            Box() {
                Text(
                    modifier = Modifier
                        .padding(start = 60.dp, top = 18.dp, bottom = 18.dp),
                    text = "Загрузить изображение",
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onSecondary
                )
                Column(modifier = Modifier
                    .clickable {
                        galleryLauncher.launch("image/*")
                    }
                    .fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.End),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colors.onPrimary.copy(alpha = .1f)
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp),
                            imageVector = Icons.Outlined.Panorama,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSecondary,
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                .align(Alignment.CenterHorizontally),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.secondary
        ) {
            Box() {
                Text(
                    modifier = Modifier
                        .padding(start = 60.dp, top = 18.dp, bottom = 18.dp),
                    text = "Сфотографировать объект",
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onSecondary
                )
                Column(modifier = Modifier
                    .clickable {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d(TAG, "OwnImageScreen: launching ")
                            viewModel.onTriggerEvent(OwnImageEvent.CreateImageUri(context) {
                                cameraLauncher.launch(it)
                            })
                        } else {
                            requestCameraPermission.launch(Manifest.permission.CAMERA)
                        }
                    }
                    .fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.End),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colors.onPrimary.copy(alpha = .1f)
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp),
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSecondary,
                        )
                    }
                }
            }
        }
        imageUri?.let {
            Log.d(TAG, "OwnImageScreen: imageAvailable")
            try {
                image = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images
                        .Media.getBitmap(context.contentResolver,it)
                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver,it)
                    ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)
                }
            }catch (e: Exception) {

                e.printStackTrace()
            }

            if (!isImageAlreadyProcessed) {
                val resizedBitmap = image?.let { it1 ->
                    Bitmap.createScaledBitmap(
                        it1,
                        imageWidthPx.toInt(),
                        imageHeightPx.toInt(),
                        false
                    )
                }
                resizedBitmap?.let {
                    viewModel.onTriggerEvent(OwnImageEvent.DetectClothesLocal(context, resizedBitmap))
                }
            }

        }
    }
}