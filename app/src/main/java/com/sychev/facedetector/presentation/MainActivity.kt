package com.sychev.facedetector.presentation

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sychev.facedetector.R
import com.sychev.facedetector.service.FaceDetectorService
import dagger.hilt.android.AndroidEntryPoint

const val OVERLAY_PERMISSION_REQUEST_CODE = 2001
const val MEDIA_PROJECTION_REQUEST_CODE = 21

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_main)
//        startAssistantService()
    }


        private fun launchMediaProjection() {
            val projectionManager =
                applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            getMediaProjection.launch(projectionManager.createScreenCaptureIntent())
        }

        private fun startAssistantService() {
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
        startAssistantService()
    }
}















