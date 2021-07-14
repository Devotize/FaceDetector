package com.sychev.facedetector.interactors.clothes_list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.exp

class DetectClothesLocal {


    fun execute(context: Context, bitmap: Bitmap): Flow<DataState<List<RectF>>> = flow<DataState<List<RectF>>>{
        try {
            emit(DataState.loading())

//            emit(DataState.success(detectClothes(context, bitmap)))

            emit(DataState.success(listOf(
                RectF(100f,100f,200f,200f),
                RectF(200f,200f,300f,300f),
                RectF(400f,800f,500f,900f),
                RectF(600f,900f,700f,1000f),
            )
            ))

        }catch (e: Exception){
            Log.d(TAG, "execute: error -> ${e.message}")
            emit(DataState.error("${e.message}"))
        }
    }



    private fun detectClothes(context: Context, bitmap: Bitmap,): List<RectF> {
        Log.d(TAG, "detectClothes: called")
        //        ClothesTestModel model = ClothesTestModel.newInstance(context);
        val ip = ImageProcessor.Builder()
            .add(ResizeOp(416, 416, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        val ti = TensorImage(DataType.FLOAT32)
        ti.load(bitmap)
        val resizedImage = ip.process(ti)
        val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 416, 416, 3), DataType.FLOAT32)
        inputBuffer.loadBuffer(resizedImage.buffer)
        val OUTPUT_WIDTH = 2535
        val labelSize = 13
        val outputMap: MutableMap<Int, Any> = java.util.HashMap()
        outputMap[0] = Array(1) {
            Array(OUTPUT_WIDTH) {
                FloatArray(
                    4
                )
            }
        }
        outputMap[1] = Array(1) {
            Array(OUTPUT_WIDTH) {
                FloatArray(
                    labelSize
                )
            }
        }
        val tfliteModel = FileUtil.loadMappedFile(context, "ClothesTestModel.tflite")
        val tfliteInterpreter = Interpreter(tfliteModel, Interpreter.Options())
        tfliteInterpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer.buffer), outputMap)
        Log.d(TAG, "detectClothes: outputs: $outputMap")
        val bboxes = outputMap[0] as Array<Array<FloatArray>>?
        val outScores = outputMap[1] as Array<Array<FloatArray>>?
        val rects = ArrayList<RectF>()
        for (i in 0 until OUTPUT_WIDTH) {
            var maxClass = 0.0f
            var detectedClass = -1
            val classes = FloatArray(labelSize)
//            Log.d(TAG, "detectClothes: ${outScores!![0][i].toList()}")
            for (c in 0 until labelSize) {
//                Log.d(TAG, "detectClothes: ${activation(outScores!![0][i]).toList()}")
//                val activatedScores = activation(outScores!![0][i])
                val softmax = softmax(outScores!![0][i])
//                Log.d(TAG, "detectClothes: softmax = ${softmax.toList()}")
                classes[c] = softmax[c].toFloat()
//                classes[c] = outScores!![0][i][c]
            }
            for (c in 0 until labelSize) {
                if (classes[c] > maxClass) {
                    detectedClass = c
                    maxClass = classes[c]
                }
            }
            val score = maxClass
//            val score = softmax(maxClass.toDouble(), outScores!![0][i])


            if (score > 0.0) {
                Log.d(TAG, "detectClothes: score = $score")
                val xPos = bboxes!![0][i][0]
                val yPos = bboxes[0][i][1]
                val w = bboxes[0][i][2]
                val h = bboxes[0][i][3]
                val rectF = RectF(
                    Math.max(0f, xPos - w / 2),
                    Math.max(0f, yPos - h / 2),
                    Math.min((bitmap.width - 1).toFloat(), xPos + w / 2),
                    Math.min((bitmap.height - 1).toFloat(), yPos + h / 2)
                )
                if (rectF.left > 200 && rectF.left < 350 && rectF.top > 500) {
                    Log.d(TAG, "detectClothes: rectF: ${rectF}")
                }
//                Log.d(TAG, "detectClothes: rectF: $rectF")
                rects.add(rectF)
            }
        }
        return rects
    }

    fun activation(input: FloatArray): FloatArray {
        val exp = FloatArray(input.size)
        var sum = 0.0f
        for (neuron in exp.indices) {
            exp[neuron] = exp(input[neuron].toDouble()).toFloat()
            sum += exp[neuron]
        }
        val output = FloatArray(input.size)
        for (neuron in output.indices) {
            output[neuron] = (exp[neuron] / sum)
        }
        return output
    }

//    fun derivative(input: FloatArray): FloatArray {
//        val softmax: FloatArray = activation(input)
//        val output = FloatArray(input.size)
//        for (neuron in output.indices) {
//            output[neuron] = (softmax[neuron] * (1.0 - softmax[neuron])).toFloat()
//        }
//        return output
//    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun softmax(input: Double, neuronValues: FloatArray): Double {
        val doubleNeurons = neuronValues.map {
            it.toDouble()
        }.toDoubleArray()
        val total = Arrays.stream(doubleNeurons).map { a: Double ->
            Math.exp(
                a
            )
        }.sum()
        return Math.exp(input) / total
    }

    private fun softmax(array: FloatArray): DoubleArray {
        val doubleArray = array.map {
            it.toDouble()
        }.toDoubleArray()
        val max = max(doubleArray)
        for (i in doubleArray.indices) {
            doubleArray[i] = doubleArray[i] - max
        }
        var sum: Double = 0.0
        val result = DoubleArray(doubleArray.size)
        for (i in 0 until doubleArray.size) {
            sum += Math.exp(array[i].toDouble())
        }
        for (i in result.indices) {
            result[i] = Math.exp(array[i].toDouble()) / sum
        }
        return result
    }

    private fun max(array: DoubleArray): Double {
        var result = Double.MIN_VALUE
        for (i in array.indices) {
            if (array[i] > result) result = array[i]
        }
        return result
    }

}