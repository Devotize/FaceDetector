package com.sychev.facedetector.interactors.clothes_list

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.Log
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.interactors.gender.DefineGender
import com.sychev.facedetector.utils.MIN_SCORE
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class DetectClothesLocal {


    fun execute(context: Context, bitmap: Bitmap): Flow<DataState<List<DetectedClothes>>> = flow<DataState<List<DetectedClothes>>>{
        try {
            emit(DataState.loading())

            emit(DataState.success(detectClothes(context, bitmap)))

        }catch (e: Exception){
            Log.d(TAG, "execute: error -> ${e.localizedMessage}")
            emit(DataState.error("${e.message}"))
        }
    }



    private fun detectClothes(context: Context, bitmap: Bitmap): ArrayList<DetectedClothes> {
        val detections: ArrayList<DetectedClothes> = ArrayList<DetectedClothes>()
        Log.d(TAG, "detectClothes: called")
        //initializin labels
        val labels = Vector<String>()
        val labelFilename = "file:///android_asset/cloths.txt"
        val actualFilename = labelFilename.split("file:///android_asset/").toTypedArray()[1]
        val labelsInput = context.assets.open(actualFilename)
        val br = BufferedReader(InputStreamReader(labelsInput))
        var line: String? = br.readLine()
        while (line != null) {
            labels.add(line)
            line = br.readLine()
        }
        br.close()
        val transformedBitmap = processBitmap(bitmap)

        val OUTPUT_WIDTH = 2535
        val tfliteModel = FileUtil.loadMappedFile(context, "yolov4-tiny_clothes_416_weights.tflite")

        val tfliteInterpreter = Interpreter(tfliteModel, Interpreter.Options())
        val byteBuffer = convertBitmapToByteBuffer(transformedBitmap)

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
                    labels.size
                )
            }
        }
        val inputArray = arrayOf<Any>(byteBuffer)

        tfliteInterpreter.runForMultipleInputsOutputs(inputArray, outputMap)
        val gender = DefineGender.defineGender(context, bitmap)


        val bboxes = outputMap[0] as Array<Array<FloatArray>>?
        val outScores = outputMap[1] as Array<Array<FloatArray>>?

        val scaleX: Float = (bitmap.width.toFloat() / transformedBitmap.width.toFloat())
        val scaleY: Float = (bitmap.height.toFloat() / transformedBitmap.height.toFloat())

        for (i in 0 until OUTPUT_WIDTH) {
            var maxClass = 0f
            var detectedClass = -1
            val classes = FloatArray(labels.size)
            for (c in labels.indices) {
                classes[c] = outScores!![0][i][c]
            }
            for (c in labels.indices) {
                if (classes[c] > maxClass) {
                    detectedClass = c
                    maxClass = classes[c]
                }
            }
            val score = maxClass

            if (score > MIN_SCORE) {
                val xPos = bboxes!![0][i][0]
                val yPos = bboxes[0][i][1]
                val w = bboxes[0][i][2]
                val h = bboxes[0][i][3]
                val rectF = RectF(
                    Math.max(0f, xPos - w / 2) * (scaleX),
                    Math.max(0f, yPos - h / 2) * (scaleY),
                    Math.min((transformedBitmap.width - 1).toFloat(), xPos + w / 2) * (scaleX),
                    Math.min((transformedBitmap.height - 1).toFloat(), yPos + h / 2) * (scaleY)
                )
                Log.d(TAG, "detectClothes: rect: $rectF, label: ${labels[detectedClass]}")
                val croppedBitmap = Bitmap.createBitmap(bitmap, rectF.left.toInt(), rectF.top.toInt(), rectF.width().toInt(), rectF.height().toInt())
                var addToDetections = true
                detections.forEach {
                    if (it.title == labels[detectedClass] && it.detectedClass == detectedClass) {
                        if (it.location.centerX() <= rectF.centerX() + 150 || it.location.centerX() >= rectF.centerX() - 150) {
                            if (it.location.centerY() <= rectF.centerY() +  150f || it.location.centerY() >= rectF.centerY() - 150f) {
                                addToDetections = false
                            }
                        }
                    }
                }
                if (addToDetections) {
                    detections.add(DetectedClothes(
                        id = i.toString(),
                        title = labels[detectedClass],
                        confidence = score,
                        location = rectF,
                        detectedClass = detectedClass,
                        sourceBitmap = bitmap,
                        croppedBitmap = croppedBitmap,
                        gender = gender,
                    ))
                }
            } else {
            }
        }
        return detections
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val BATCH_SIZE = 1
        val INPUT_SIZE = 416
        val PIXEL_SIZE = 3

        val byteBuffer =
            ByteBuffer.allocateDirect(4 * BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat((`val` shr 16 and 0xFF) / 255.0f)
                byteBuffer.putFloat((`val` shr 8 and 0xFF) / 255.0f)
                byteBuffer.putFloat((`val` and 0xFF) / 255.0f)
            }
        }
        return byteBuffer
    }

    private fun processBitmap(source: Bitmap): Bitmap {
        val size = 416
        val height = source.height
        val width = source.width
        val croppedBitmap = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888)
        val frameToCropTransformations = getTransformationMatrix(width,height,size,size,0,false)
        val cropToFrameTransformations = Matrix()
        frameToCropTransformations?.invert(cropToFrameTransformations)

        val canvas = Canvas(croppedBitmap)
        if (frameToCropTransformations != null) {
            canvas.drawBitmap(source, frameToCropTransformations, null)
        }
        return croppedBitmap
    }

    private fun getTransformationMatrix(
        srcWidth: Int,
        srcHeight: Int,
        dstWidth: Int,
        dstHeight: Int,
        applyRotation: Int,
        maintainAspectRatio: Boolean
    ): Matrix? {
        val matrix = Matrix()
        if (applyRotation != 0) {
            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

        // Account for the already applied rotation, if any, and then determine how
        // much scaling is needed for each axis.
        val transpose = (Math.abs(applyRotation) + 90) % 180 == 0
        val inWidth = if (transpose) srcHeight else srcWidth
        val inHeight = if (transpose) srcWidth else srcHeight

        // Apply scaling if necessary.
        if (inWidth != dstWidth || inHeight != dstHeight) {
            val scaleFactorX = dstWidth / inWidth.toFloat()
            val scaleFactorY = dstHeight / inHeight.toFloat()
            if (maintainAspectRatio) {
                // Scale by minimum factor so that dst is filled completely while
                // maintaining the aspect ratio. Some image may fall off the edge.
                val scaleFactor = Math.max(scaleFactorX, scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            } else {
                // Scale exactly to fill dst from src.
                matrix.postScale(scaleFactorX, scaleFactorY)
            }
        }
        if (applyRotation != 0) {
            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }
        return matrix
    }

}