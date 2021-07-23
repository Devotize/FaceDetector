package com.sychev.facedetector.presentation.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CameraActivity: AppCompatActivity() {

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivity(intent)
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionRequest.launch(Manifest.permission.CAMERA)

    }
}