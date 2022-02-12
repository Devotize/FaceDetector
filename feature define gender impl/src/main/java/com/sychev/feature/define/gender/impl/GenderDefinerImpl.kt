package com.sychev.feature.define.gender.impl

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.sychev.feature.deafine.gender.api.CommonGender
import com.sychev.feature.deafine.gender.api.Gender
import com.sychev.feature.deafine.gender.api.GenderDefiner
import com.sychev.utils.getAssetFilePath
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import javax.inject.Inject
import kotlin.math.exp

internal class GenderDefinerImpl @Inject constructor(private val context: Context): GenderDefiner {

    private val model = loadDefinerModel()

    override fun defineGender(bitmap: Bitmap): CommonGender {
        val tensorInput = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB,
            MemoryFormat.CHANNELS_LAST
        )
        val tensorOutput = model.forward(IValue.from(tensorInput)).toTensor()

        val scores: FloatArray = tensorOutput.dataAsFloatArray
        var maxScore: Double = -Double.MAX_VALUE
        var maxScoreIdx: Int = -1
        for (i in scores.indices) {
            val score = softmax(scores[i].toDouble(), scores)
            if (scores[i] > maxScore) {
                maxScore = score
                maxScoreIdx = i
            }
        }
        val gender: Gender = if (maxScoreIdx == 0) Gender.MALE else Gender.FEMALE
        Log.d(TAG, "Result of processing: ${gender.value}")
        return CommonGender(
            gender
        )
    }

    private fun loadDefinerModel(): Module =
        LiteModuleLoader
            .load(getAssetFilePath(context, GENDER_DEFINER_MODEL_NAME))


    private fun softmax(input: Double, neuronValues: FloatArray): Double {
        val total = neuronValues.map { a: Float ->
            exp(
                a
            )
        }.sum()
        return Math.exp(input) / total
    }


    companion object {
        fun getInstance(context: Context) = GenderDefinerImpl(context)
        private const val GENDER_DEFINER_MODEL_NAME = "define_gender_model_v2.ptl"
        private const val TAG = "GENDER DEFINER"
    }
}