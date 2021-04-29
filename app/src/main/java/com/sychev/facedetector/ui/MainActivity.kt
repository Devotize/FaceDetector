package com.sychev.facedetector.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.sychev.facedetector.R
import com.sychev.facedetector.service.FaceDetectorService
import com.sychev.facedetector.utils.TAG

const val OVERLAY_PERMISSION_REQUEST_CODE = 2001
const val MEDIA_PROJECTION_REQUEST_CODE = 21

class MainActivity : AppCompatActivity() {

    private val getDrawOverlays = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result: ActivityResult ->
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                launchMediaProjection()
            }
        }
    }

    private val getMediaProjection = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK){
            val intent = Intent(applicationContext, FaceDetectorService::class.java)
            intent.putExtra("data", result.data)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!Settings.canDrawOverlays(this)){
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                getDrawOverlays.launch(intent)
            } else {
                launchMediaProjection()
            }
        } else {
            launchMediaProjection()
        }

    }

    private fun launchMediaProjection() {
        val projectionManager = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        getMediaProjection.launch(projectionManager.createScreenCaptureIntent())
    }

}
















