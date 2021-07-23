package com.sychev.facedetector.presentation.activity

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sychev.facedetector.R
import com.sychev.facedetector.presentation.ui.components.BottomNavigationBar
import com.sychev.facedetector.presentation.ui.main.MainFragmentViewModel
import com.sychev.facedetector.presentation.ui.screen.ClothesListScreen
import com.sychev.facedetector.presentation.ui.screen.Screen
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import com.sychev.facedetector.service.FaceDetectorService
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.AndroidEntryPoint

const val OVERLAY_PERMISSION_REQUEST_CODE = 2001
const val MEDIA_PROJECTION_REQUEST_CODE = 21

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainFragmentViewModel by viewModels()

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
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stopIntent = Intent(applicationContext, FaceDetectorService::class.java)
        stopService(stopIntent)
        val bundle = intent.extras
        viewModel.hugeFirstElement.value = bundle?.getBoolean("from_assistant_launch") ?: false

        setContent {
            val navController = rememberNavController()
            AppTheme {
                Scaffold(
                    bottomBar = {BottomNavigationBar(navController = navController)},
                ) {
                    NavHost(navController, startDestination = Screen.ClothesList.route, Modifier.padding(it)) {
                        composable(Screen.ClothesList.route){
                            ClothesListScreen(viewModel = viewModel, launcher = this@MainActivity)
                        }
                        composable(Screen.FavoriteClothesList.route){
                            Text(text = "favorite clothes list")
                        }
                        composable(Screen.Shop.route){
                            Text(text = "Shop")
                        }
                        composable(Screen.Profile.route){
                            Text(text = "Profile")
                        }
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

}















