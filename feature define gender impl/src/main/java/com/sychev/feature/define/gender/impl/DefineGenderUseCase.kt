package com.sychev.feature.define.gender.impl

import android.graphics.Bitmap
import com.sychev.feature.deafine.gender.api.CommonGender
import com.sychev.feature.deafine.gender.api.GenderDefiner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefineGenderUseCase @Inject constructor(
    private val definer: GenderDefiner
) {
    fun defineGender(bitmap: Bitmap): Flow<CommonGender> = flow {
        emit(definer.defineGender(bitmap))
    }
}