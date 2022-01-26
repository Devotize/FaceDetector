package com.sychev.facedetector.interactors.gender

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.domain.filter.FilterValues
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
import kotlin.math.exp


class DefineGender {

    fun execute(context: Context, bitmap: Bitmap): Flow<DataState<String>> = flow {
        try {
            emit(DataState.loading())
            val result = defineGender(context, bitmap)

            emit(DataState.success(result))

        }catch (e: Exception) {
            emit(DataState.error("error: ${e.localizedMessage}"))
            e.printStackTrace()
        }
    }

    companion object{
        fun defineGender(context: Context, bitmap: Bitmap): String {
            val module = LiteModuleLoader.load(assetFilePath(context, "mobile_model-2.ptl"))
            Log.d(TAG, "execute: module = $module")
            val tensorInput = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST)
            val tensorOutput = module.forward(IValue.from(tensorInput)).toTensor()

            val scores: FloatArray = tensorOutput.dataAsFloatArray
            var maxScore: Double = -Double.MAX_VALUE
            var maxScoreIdx: Int = -1
            for (i in scores.indices) {
                val score = softmax(scores[i].toDouble(), scores)
//                Log.d(TAG, "execute: score $i: $score")
                if (scores[i] > maxScore) {
                    maxScore = score
                    maxScoreIdx = i
                }
            }
            val result: String = if (maxScoreIdx == 0) FilterValues.Constants.Gender.male else FilterValues.Constants.Gender.female
            Log.d(TAG, "defineGender: result: $result")
            return result
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




}












