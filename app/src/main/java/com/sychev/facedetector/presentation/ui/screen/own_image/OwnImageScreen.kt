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
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Panorama
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.interactors.image.InsertImageToCache
import com.sychev.facedetector.presentation.ui.screen.own_image.components.ChangeCurrentImageButton
import com.sychev.facedetector.presentation.ui.screen.own_image.components.FoundedClothesCardExtended
import com.sychev.facedetector.presentation.ui.screen.own_image.components.FoundedClothesCardSmall
import com.sychev.facedetector.utils.TAG
import com.sychev.facedetector.utils.toBitmap
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class)
@Composable
fun OwnImageScreen(
    viewModel: OwnImageViewModel,
    goToRetailScreen: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageUri = viewModel.imageUri.value
    val foundedClothes = viewModel.foundedClothes
    val selectedClothesList = viewModel.selectedClothesList.value
    var imageWidthPx by remember {
        mutableStateOf(0f)
    }
    var imageHeightPx by remember {
        mutableStateOf(0f)
    }
    val isImageAlreadyProcessed = viewModel.isImageAlreadyProcessed.value
    val images = viewModel.images
    val selectedImageIndex = viewModel.selectedImageIndex.value
    val horizontalPagerOwnPhotosState =
        rememberPagerState(pageCount = images.size, selectedImageIndex).also {
            it.currentPage.let {
                viewModel.selectedImageIndex.value = it
            }
        }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.onTriggerEvent(
                    OwnImageEvent.InsertBitmapInCache(
                        bitmap = uri.toBitmap(context),
                        context
                    )
                )
                viewModel.imageUri.value = uri
                viewModel.isImageAlreadyProcessed.value = false
            }

        }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                Log.d(TAG, "OwnImageScreen: picture activity reslut: $it")
                val uri = viewModel.imageUri.value
                viewModel.onTriggerEvent(
                    OwnImageEvent.InsertBitmapInCache(
                        bitmap = uri!!.toBitmap(
                            context
                        ), context
                    )
                )
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

    Box(modifier = Modifier.fillMaxSize()) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(top = 4.dp, start = 6.dp, end = 6.dp),
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Center)
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxHeight()
                            .wrapContentWidth(),
                        color = MaterialTheme.colors.primary,
                        shape = MaterialTheme.shapes.large,
                        elevation = 0.dp
                    ) {
                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            state = horizontalPagerOwnPhotosState,
                        ) { page ->
                            images[page].let { imageData ->
                                Box {
                                    if (!imageData.isProcessed && imageWidthPx > 0f) {
                                        val resizedBitmap = imageData.imageData.let {
                                            Bitmap.createScaledBitmap(
                                                it.bitmap,
                                                imageWidthPx.toInt(),
                                                imageHeightPx.toInt(),
                                                false
                                            )
                                        }
                                        resizedBitmap?.let {
                                            viewModel.onTriggerEvent(
                                                OwnImageEvent.DetectClothesLocal(
                                                    context,
                                                    resizedBitmap,
                                                    imageData,
                                                )
                                            )
                                        }
                                        imageData.isProcessed = true
                                    }
                                    Image(
                                        modifier = Modifier
                                            .onGloballyPositioned {
                                                imageWidthPx = it.size.toSize().width
                                                imageHeightPx = it.size.toSize().height
                                            }
                                            .fillMaxSize(),
                                        bitmap = imageData.imageData.bitmap.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center
                                    )
                                    imageData.clothes.forEach {
                                        Log.d(TAG, "OwnImageScreen: wrappedClothes: $it")
                                        FoundedClothesCardSmall(
                                            location = it.detectedClothes.location,
                                            clothes = it.clothes[0],
                                            onClick = {
                                                viewModel.onSelectedClothesChange(
                                                    images[selectedImageIndex],
                                                    it
                                                )
                                            }
                                        )
                                    }
                                    imageData.selectedClothes.let { wc ->
                                        wc?.let {
                                            FoundedClothesCardExtended(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(94.dp)
                                                    .align(Alignment.BottomCenter),
                                                foundedClothes = it.clothes,
                                                onClick = {
                                                    goToRetailScreen()
                                                },
                                                onCloseClick = {
//                                    viewModel.onSelectedClothesChange(it, null)
                                                },
                                                onGenderChange = { gender ->
                                                    viewModel.onTriggerEvent(
                                                        OwnImageEvent.OnGenderChange(
                                                            gender,
                                                            images[selectedImageIndex],
                                                            wc,
                                                            context
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }
                                    imageData.let {
                                        if (it.showNothingFound) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.BottomCenter)
                                                    .background(color = MaterialTheme.colors.onPrimary),
                                            ) {
                                                Text(
                                                    modifier = Modifier
                                                        .padding(8.dp)
                                                        .align(Alignment.Center),
                                                    text = "Ничего не найдено",
                                                    style = MaterialTheme.typography.h6,
                                                    color = MaterialTheme.colors.primary
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary
                )
            ) {
                Text(
                    text = "Галлерея",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.h3
                )
            }
        }
        if (images.lastIndex != selectedImageIndex && images.size > 1) {
            //nextButton
            ChangeCurrentImageButton(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .align(Alignment.CenterEnd),
                isNext = true
            ) {
//                viewModel.onGoToNextImage()
                scope.launch {
                    horizontalPagerOwnPhotosState.animateScrollToPage(horizontalPagerOwnPhotosState.currentPage + 1)

                }
            }
        }
        if (0 != selectedImageIndex && images.size > 1) {
            //previous button
            ChangeCurrentImageButton(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .align(Alignment.CenterStart),
                isNext = false
            ) {
//                viewModel.onGoToPreviousImage()
                scope.launch {
                    horizontalPagerOwnPhotosState.animateScrollToPage(horizontalPagerOwnPhotosState.currentPage - 1)
                }
            }
        }

    }
}