package com.sychev.facedetector.presentation.ui.screen.own_image

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sychev.facedetector.R
import com.sychev.facedetector.presentation.ui.screen.own_image.components.FoundedClothesCardExtended
import com.sychev.facedetector.presentation.ui.screen.own_image.components.FoundedClothesCardSmall
import com.sychev.facedetector.utils.TAG


@Composable
fun OwnImageScreen(
    viewModel: OwnImageViewModel,
    goToRetailScreen: () -> Unit,
) {
    val context = LocalContext.current
    val imageUri = viewModel.imageUri.value
    val foundedClothes = viewModel.foundedClothes
    val selectedClothesList = viewModel.selectedClothesList
    var imageWidthPx by remember {
        mutableStateOf(0f)
    }
    var imageHeightPx by remember {
        mutableStateOf(0f)
    }
    val isImageAlreadyProcessed = viewModel.isImageAlreadyProcessed.value

    var image by remember{mutableStateOf<Bitmap?>(BitmapFactory.decodeResource(context.resources, R.drawable.default_own_img))}
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp, bottom = 0.dp),
            text = "Коснитесь, чтобы посмотреть товары и узнать стоимость",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onPrimary,
        )
        BoxWithConstraints(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight(0.85f)
                .padding(top = 4.dp, start = 6.dp, end = 6.dp),
        ) {
            imageWidthPx = with(LocalDensity.current){this@BoxWithConstraints.maxWidth.toPx()}
            imageHeightPx = with(LocalDensity.current){this@BoxWithConstraints.maxHeight.toPx()}
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(),
                color = MaterialTheme.colors.primary,
                shape = MaterialTheme.shapes.large,
                elevation = 0.dp
            ) {
                image?.let {
                    if (!isImageAlreadyProcessed) {
                        viewModel.isImageAlreadyProcessed.value = true
                        val resizedBitmap = it.let { it1 ->
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
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Inside
                    )
                }



            }
            foundedClothes.forEach {
                FoundedClothesCardSmall(
                    location = it.location,
                    clothes = it.clothes[0],
                    onClick = {
                        viewModel.onSelectedClothesListChange(it.clothes)
                    }
                )
            }
            if (selectedClothesList.isNotEmpty()) {
                FoundedClothesCardExtended(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(94.dp)
                        .align(Alignment.BottomCenter)
                    ,
                    foundedClothes = selectedClothesList,
                    onClick = {
                        goToRetailScreen()
                    },
                    onCloseClick = {
                        viewModel.onSelectedClothesListChange(listOf())
                    },
                    onGenderChange = {

                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                modifier = Modifier
                    .width(145.dp)
                    .align(Alignment.CenterVertically),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colors.primary,
                border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
            ) {
                Box(
                    modifier = Modifier
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp),
                        text = "Галерея",
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.onPrimary
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
                            color = MaterialTheme.colors.primaryVariant
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(24.dp),
                                imageVector = Icons.Outlined.Panorama,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .width(145.dp)
                    .align(Alignment.CenterVertically),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colors.primary,
                border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
            ) {
                Box() {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp),
                        text = "Фото",
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.onPrimary
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
                            color = MaterialTheme.colors.primaryVariant
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(24.dp),
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary,
                            )
                        }
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
        }
    }
}