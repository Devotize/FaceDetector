package com.sychev.facedetector.presentation.activity

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.components.BottomNavigationBar
import com.sychev.facedetector.presentation.ui.components.GenericDialog
import com.sychev.facedetector.presentation.ui.detectorAssitant.PhotoDetector
import com.sychev.facedetector.presentation.ui.main.MainEvent
import com.sychev.facedetector.presentation.ui.main.MainFragmentViewModel
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailScreen
import com.sychev.facedetector.presentation.ui.screen.FavoriteClothesListScreen
import com.sychev.facedetector.presentation.ui.screen.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailEvent
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailViewModel
import com.sychev.facedetector.presentation.ui.screen.feed_list.FeedListScreen
import com.sychev.facedetector.presentation.ui.screen.feed_list.FeedViewModel
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.service.FaceDetectorService
import com.sychev.facedetector.utils.MessageDialog
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val OVERLAY_PERMISSION_REQUEST_CODE = 2001
const val MEDIA_PROJECTION_REQUEST_CODE = 21

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainFragmentViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()

    private val getDrawOverlays =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    launchMediaProjection()
                }
            }
        }

    private val getMediaProjection =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = Intent(applicationContext, FaceDetectorService::class.java)
                intent.putExtra("data", result.data)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
                if (mainViewModel.closeApp) finish()
            }
        }

    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stopIntent = Intent(applicationContext, FaceDetectorService::class.java)
        stopService(stopIntent)
        val bundle = intent.extras
        mainViewModel.launchFromAssistant.value =
            bundle?.getBoolean("from_assistant_launch") ?: false

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                val dialogMessages = MessageDialog.dialogMessages

                Scaffold(
                    scaffoldState = scaffoldState,
                    bottomBar = {
                            BottomNavigationBar(
                                navController = navController,
                                launchAssistant = {
                                    mainViewModel.onTriggerEvent(
                                        MainEvent.LaunchDetector(
                                            this,
                                            true
                                        )
                                    )
                                }
                            )
                    },
                    drawerContent = {
                        Text("Reviews")
                    },
                    drawerShape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 8.dp,
                        bottomEnd = 8.dp,
                        bottomStart = 0.dp
                    ),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(start = 18.dp, top = 18.dp, end = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(onClick = {
                                    Log.d(TAG, "onCreate: menu clicked")
                                    if (scaffoldState.drawerState.isClosed) {
                                        scope.launch { scaffoldState.drawerState.open() }
                                    } else {
                                        scope.launch { scaffoldState.drawerState.close() }
                                    }

                                }) {
                                    Icon(
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(30.dp),
                                        imageVector = Icons.Outlined.Menu,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            this@MainActivity.finish()
                                        },
                                    text = "Close",
                                    color = MaterialTheme.colors.onBackground,
                                    style = MaterialTheme.typography.h3,
                                )
                            }
                        NavHost(
                            navController,
                            startDestination = Screen.FeedList.route,
                            Modifier.padding(it)
                        ) {
//                            composable(Screen.ClothesListStart.route) {
//                                FavoriteClothesListScreen(
//                                    viewModel = mainViewModel,
//                                    navController = navController,
//                                )
//                            }
                            composable(Screen.FavoriteClothesList.route) {
                                FavoriteClothesListScreen(
                                    viewModel = mainViewModel,
                                    navController = navController,
                                )
                            }
                            composable(Screen.Profile.route) {
                                Text(text = "Profile")
                            }
                            composable(Screen.ClothesListRetail.route) {

                            }
                            composable(Screen.FeedList.route) {
                                FeedListScreen(viewModel = feedViewModel)
                            }
                        }
                    }
                }
                    if(dialogMessages.isNotEmpty()) {
                        dialogMessages.forEach {
                            GenericDialog(
                                title = it.title,
                                message = it.message,
                                onDismiss = {
                                     it.onDismiss()
                                },
                                onPositiveAction = {
                                    it.onPositiveAction()
                                }
                            )
                        }
                    }
            }

        }

    }
    private fun launchMediaProjection() {
        val projectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        getMediaProjection.launch(projectionManager.createScreenCaptureIntent())
    }

    fun startAssistantService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                getDrawOverlays.launch(intent)
            } else {
                launchMediaProjection()
            }
        } else {
            launchMediaProjection()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PhotoDetector.insideApp = false
    }

}















