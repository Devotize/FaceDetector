package com.sychev.feature.deafine.gender.api

import android.graphics.Bitmap

interface GenderDefiner {
    fun defineGender(bitmap: Bitmap): CommonGender
}