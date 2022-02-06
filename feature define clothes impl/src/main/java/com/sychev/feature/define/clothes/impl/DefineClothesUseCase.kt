package com.sychev.feature.define.clothes.impl

import android.graphics.Bitmap
import com.sychev.feature.define.clothes.api.ClothesDefiner
import com.sychev.feature.define.clothes.api.DetectedClothes
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class DefineClothesUseCase @Inject constructor(
    private val definer: ClothesDefiner
) {

    fun defineClothes(bitmap: Bitmap) = flow<DetectedClothes> {
        val result = definer.defineClothes(bitmap)
        emit(result)
    }

}