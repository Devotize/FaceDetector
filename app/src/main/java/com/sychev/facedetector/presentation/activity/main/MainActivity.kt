package com.sychev.facedetector.presentation.activity.main

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.ui.components.BottomNavigationBar
import com.sychev.facedetector.presentation.ui.components.EnterAnimation
import com.sychev.facedetector.presentation.ui.components.GenericDialog
import com.sychev.facedetector.presentation.ui.detectorAssitant.AssistantDetector
import com.sychev.facedetector.presentation.ui.detectorAssitant.AssistantManager
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.screen.FavoriteClothesListScreen
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.ClothesDetailScreen
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.ClothesDetailViewModel
import com.sychev.facedetector.presentation.ui.screen.clothes_list_favorite.FavoriteClothesListViewModel
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailScreen
import com.sychev.facedetector.presentation.ui.screen.clothes_list_retail.ClothesListRetailViewModel
import com.sychev.facedetector.presentation.ui.screen.feed_list.FeedListScreen
import com.sychev.facedetector.presentation.ui.screen.feed_list.FeedViewModel
import com.sychev.facedetector.presentation.ui.screen.own_image.OwnImageScreen
import com.sychev.facedetector.presentation.ui.screen.own_image.OwnImageViewModel
import com.sychev.facedetector.presentation.ui.screen.shop.ShopScreen
import com.sychev.facedetector.presentation.ui.screen.shop.ShopViewModel
import com.sychev.facedetector.presentation.ui.screen.shop.filters_screen.FiltersScreen
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.service.FaceDetectorService
import com.sychev.facedetector.utils.MessageDialog
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val OVERLAY_PERMISSION_REQUEST_CODE = 2001
const val MEDIA_PROJECTION_REQUEST_CODE = 21

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val shopViewModel: ShopViewModel by viewModels()
    private val retailViewModel: ClothesListRetailViewModel by viewModels()
    private val ownImageViewModel: OwnImageViewModel by viewModels()

    @Inject
    lateinit var navigationManager: NavigationManager
    @Inject
    lateinit var assistantManager: AssistantManager

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
//        val stopIntent = Intent(applicationContext, FaceDetectorService::class.java)
//        stopService(stopIntent)
        mainViewModel.onTriggerEvent(MainEvent.GetFilterValues)
        assistantManager.onActiveStatusChange(false)
        val bundle = intent.extras
        mainViewModel.launchFromAssistant.value =
            bundle?.getBoolean("from_assistant_launch") ?: false
        var firstLaunch = true
        mainViewModel.onTriggerEvent(MainEvent.GetDetectedClothesEvent)
        supportFragmentManager

        setContent {

            AppTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                val dialogMessages = MessageDialog.dialogMessages
                var hasNavBottomBar by remember{ mutableStateOf(true)}


                Scaffold(
                    scaffoldState = scaffoldState,
                    bottomBar = {
                        if (hasNavBottomBar) {
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
                        }
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colors.primary)
                            .fillMaxSize()
                    ) {
                        NavHost(
                            navController,
                            startDestination = Screen.FeedList.route,
                            Modifier.padding(it)
                        ) {
                            composable(Screen.FavoriteClothesList.route) {
                                val viewModel = hiltViewModel<FavoriteClothesListViewModel>(
                                    navController.getBackStackEntry(Screen.FavoriteClothesList.route)
                                )
                                hasNavBottomBar = true
                                FavoriteClothesListScreen(
                                    viewModel = viewModel,
                                )
                            }
                            composable(Screen.Shop.route) {
                                hasNavBottomBar = true
                                this@MainActivity.shopViewModel.clothesList.clear()
                                ShopScreen(viewModel = this@MainActivity.shopViewModel)
                            }

                            composable(Screen.FiltersScreen.route) {
                                hasNavBottomBar = false
                                EnterAnimation {
                                    FiltersScreen(viewModel = this@MainActivity.shopViewModel)
                                }
                            }

                            composable(Screen.OwnImage.route) {
                                hasNavBottomBar = true
                                OwnImageScreen(viewModel = ownImageViewModel)
                            }
                            composable(Screen.ClothesListRetail.route) {

                            }
                            composable(Screen.FeedList.route) { backStackEntry ->
                                val fvm: FeedViewModel = hiltViewModel<FeedViewModel>(
                                    navController.getBackStackEntry(Screen.FeedList.route)
                                )
                                hasNavBottomBar = true
                                FeedListScreen(viewModel = fvm)
                            }
                            composable(
                                route = Screen.ClothesDetail.route,
                            ) { backStackEntry ->
                                hasNavBottomBar = true
                                Log.d(TAG, "onCreate: destination: DetailScreen")
                                navController.previousBackStackEntry?.arguments?.getParcelableArrayList<Clothes>(
                                    "args"
                                )?.let {
                                    val detailViewModel = hiltViewModel<ClothesDetailViewModel>(navController.getBackStackEntry(Screen.ClothesDetail.route))
                                    EnterAnimation {
                                        ClothesDetailScreen(
                                            clothes = it[0],
                                            viewModel = detailViewModel,
                                            onBackClick = { onBackPressed() }
                                        )
                                    }
                                }
                            }
                            composable(Screen.ClothesListRetail.route) { navBackStackEntry ->
                                hasNavBottomBar = false
                                var clothesList = mutableListOf<DetectedClothes>()

                                if (mainViewModel.detectedClothesList.isNotEmpty()) {
                                    clothesList = mainViewModel.detectedClothesList
                                }
                                navController.previousBackStackEntry?.arguments?.getParcelableArrayList<DetectedClothes>(
                                    "args"
                                )?.let {
                                    Log.d(TAG, "onCreate: retailArgs: $it")
                                    clothesList = it
                                }

                                val retailViewModel = hiltViewModel<ClothesListRetailViewModel>(navController.getBackStackEntry(Screen.ClothesListRetail.route))
                                EnterAnimation {
                                    ClothesListRetailScreen(
                                        viewModel = retailViewModel,
                                        detectedClothes = clothesList,
                                        onBackClick = { onBackPressed() },
                                    )
                                }
                            }
                            if (firstLaunch) {
                                    navigationManager.navigate(Screen.FavoriteClothesList)
                                    navigationManager.navigate(Screen.FeedList)
                                if (mainViewModel.detectedClothesList.isNotEmpty()) {
                                    val screen = Screen.ClothesListRetail.apply {
                                            arguments = arrayListOf<Parcelable>().apply {
                                                addAll(mainViewModel.detectedClothesList)
                                            }
                                        }
                                        navigationManager.navigate(screen)
                                }

                                    firstLaunch = false
                            }
                        }
                    }
                    if (dialogMessages.isNotEmpty()) {
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
                navigationManager.commands.value.also { pairScreenNavBuider ->
                    if (pairScreenNavBuider.first is Screen.Default) {
                        return@also
                    }
                    Log.d(TAG, "onCreate: arguments: ${pairScreenNavBuider.first.arguments}")
                    if (pairScreenNavBuider.first.arguments != null) {
                        navController.currentBackStackEntry?.arguments?.putParcelableArrayList(
                            "args",
                            pairScreenNavBuider.first.arguments
                        )
                    }
                    try {
                        navController.navigate(route = pairScreenNavBuider.first.route, builder = pairScreenNavBuider.second)
                    }catch (e: Exception) {
                        e.printStackTrace()
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

    override fun onStop() {
        super.onStop()
        assistantManager.onActiveStatusChange(true)
    }

    override fun onResume() {
        super.onResume()
        assistantManager.onActiveStatusChange(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        AssistantDetector.insideApp = false
        assistantManager.onActiveStatusChange(true)
    }

}















