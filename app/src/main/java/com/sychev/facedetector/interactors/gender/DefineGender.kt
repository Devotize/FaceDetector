package com.sychev.facedetector.interactors.gender

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.ScriptGroup
import android.util.Log
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.Arrays
import java.util.function.DoubleUnaryOperator
import kotlin.math.exp


class DefineGender {

    fun execute(context: Context, bitmap: Bitmap): Flow<DataState<String>> = flow {
        try {
            val module = LiteModuleLoader.load(assetFilePath(context, "gender_android.ptl"))
//            Log.d(TAG, "execute: module = $module")
            val tensorInput = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST)
            val tensorOutput = module.forward(IValue.from(tensorInput)).toTensor()

            val scores: FloatArray = tensorOutput.dataAsFloatArray
            var maxScore: Double = -Double.MAX_VALUE
            var maxScoreIdx: Int = -1
            for (i in scores.indices) {
                val score = softmax(scores[i].toDouble(), scores)
                Log.d(TAG, "execute: score $i: $score")
                if (scores[i] > maxScore) {
                    maxScore = score
                    maxScoreIdx = i
                }
            }
            val result: String = if (maxScoreIdx == 0) "Male" else "Female"
            emit(DataState.success(result))

        }catch (e: Exception) {
            emit(DataState.error("error: ${e.localizedMessage}"))
            e.printStackTrace()
        }
    }


    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        val inputStream: InputStream = context.assets.open(assetName)
        val outputStream: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(4 * 1024)
        var read: Int = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        outputStream.flush()
        return file.absolutePath
    }

    private fun softmax(input: Double, neuronValues: FloatArray): Double {
        val total = neuronValues.map { a: Float ->
            exp(
                a
            )
        }.sum()
        return Math.exp(input) / total
    }

}












