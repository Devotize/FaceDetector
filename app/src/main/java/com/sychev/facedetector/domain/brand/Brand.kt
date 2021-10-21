package com.sychev.facedetector.domain.brand

import android.graphics.Bitmap
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

data class Brand(
    val name: String,
    val image: Bitmap?,
)