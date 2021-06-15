package com.sychev.facedetector.presentation.ui.detectorAssitant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.service.FaceDetectorService
import com.sychev.facedetector.utils.TAG
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*
import kotlin.collections.ArrayList
import java.util.Arrays
import kotlin.math.exp
import android.content.res.ColorStateList
import android.graphics.Bitmap

import com.sychev.facedetector.presentation.MainActivity
import com.sychev.facedetector.presentation.ui.items.DetectedClothesList


class PhotoDetector
    (
    private val context: Context,
    private val mediaProjection: MediaProjection,
    private val stopService: (() -> Unit)? = null,
) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PhotoFaceDetectorEntryPoint{
        fun provideRepository(): SavedScreenshotRepo
    }

    private val entryPoint = EntryPointAccessors.fromApplication(context, PhotoFaceDetectorEntryPoint::class.java)
    private val repository = entryPoint.provideRepository()
    private val detectedClothesList = DetectedClothesList(context)

    private var isAssistantShown = false
    private var widthPx = 0
    private var heightPx = 0
    private val windowManager =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).also { wm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowsMetrics = wm.currentWindowMetrics
                val windowInsets = windowsMetrics.windowInsets
                val insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
                )
                val insetsWidth = insets.right + insets.left
                val insetsHeight = insets.top + insets.bottom
                val bounds = windowsMetrics.bounds
                widthPx = bounds.width() - insetsWidth
                heightPx = bounds.height() - insetsHeight
            } else {
                val size = Point()
                val display = wm.defaultDisplay
                display.getSize(size)
                widthPx = size.x
                heightPx = size.y
            }
        }

    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val rootView = layoutInflater.inflate(R.layout.face_detector_layout, null)
    private val sheetButton = rootView.findViewById<FrameLayout>(R.id.sheet_button).apply {
        setOnClickListener {
            isAssistantShown = !isAssistantShown
            showOrHideAssistant()
            boundingBoxes.forEach {
                removeViewFromWM(it)
            }
            (frameTouchListener as FrameLayout).removeAllViews()
//            findNewFaces(0)
        }
    }
    private val assistantButtons: ArrayList<ImageButton> = ArrayList()
    private val openAppButton = rootView.findViewById<ImageButton>(R.id.app_button).apply {
        assistantButtons.add(this)
        setOnClickListener {
//            launchApp()
//            clearBoundingBoxes()

            takeScreenshot()?.let {
                CoroutineScope(IO).launch {
                    val detectedClothes = repository.detectClothes(compressInputImage(it), context)
                    Log.d(TAG, "detectedClothes: $detectedClothes")
                    withContext(Main) {
                        detectedClothesList.myAdapter.apply {
                            val startPosition = list.size
                            val itemCount = detectedClothes.size
                            list.addAll(detectedClothes)
                            notifyItemRangeInserted(startPosition, itemCount)
                        }
                        detectedClothesList.open()
                    }
                }
            }

        }
    }
    private val galleryButton = rootView.findViewById<ImageButton>(R.id.gallery_button).apply {
        assistantButtons.add(this)
        setOnClickListener {
            launchApp()
            clearBoundingBoxes()
        }
    }
    private val closeButton = rootView.findViewById<ImageButton>(R.id.close_button).apply {
        assistantButtons.add(this)
        setOnClickListener {
            removeViewFromWM(frameTouchListener)

            val intent = Intent(context, FaceDetectorService::class.java)
//            intent.putExtra(FaceDetectorService.EXIT_NAME, FaceDetectorService.EXIT_VALUE)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(intent)
//            } else {
//                context.startService(intent)
//            }
            context.stopService(intent)
        }
    }
    private val notificationPointer = rootView.findViewById<Button>(R.id.notifiсation_point).apply {
        visibility = View.GONE
    }
    private val sheetArrow = rootView.findViewById<ImageView>(R.id.sheet_arrow)
    @SuppressLint("ClickableViewAccessibility")
    private val frameTouchListener = layoutInflater.inflate(R.layout.frame_touch_listener, null).apply {
        setOnTouchListener { v, event ->
            (this as FrameLayout).removeAllViews()
//            findNewFaces(2000)
            
            
            false
        }
    }

    private val frameParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    @SuppressLint("WrongConstant")
    private val imageReader: ImageReader = ImageReader.newInstance(
        widthPx,
        heightPx,
        PixelFormat.RGBA_8888,
5
    ).also {
        createVirtualDisplay(it)
    }

    private var isDetecting = false
    private var detectFacesJob: Job = Job()
    private val faceDetection = FaceDetection.getClient()
    private val detectedFaces = ArrayList<Face>()
    private val boundingBoxes = ArrayList<View>()
    private val faceCircles = ArrayList<Button>()
    private var screenshot: Bitmap? = null


    private fun addViewToWM(view: View, params: WindowManager.LayoutParams){
        if(view.parent == null) {
            windowManager.addView(view, params)
        }
    }

    private fun removeViewFromWM(view: View){
        if (view.parent != null) {
            windowManager.removeView(view)
        }
    }

    private fun createVirtualDisplay(ir: ImageReader) {
        mediaProjection.createVirtualDisplay(
            "screen_capture",
            widthPx,
            heightPx,
            context.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            ir.surface,
            null,
            null
        )
    }

    init {
        open()
        showOrHideAssistant()
    }

    fun open() {
        addViewToWM(frameTouchListener, frameParams)
        addViewToWM(rootView, getWmLayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP or Gravity.END
        }
        )
    }

    fun close() {
        removeViewFromWM(rootView)
        removeViewFromWM(frameTouchListener)
        boundingBoxes.forEach {
            removeViewFromWM(it)
        }
    }

    private fun showOrHideAssistant(){
        if (isAssistantShown){
            sheetArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
            notificationPointer.visibility = View.GONE
            assistantButtons.forEach {
                it.visibility = View.VISIBLE
            }
        } else {
            sheetArrow.setImageResource(R.drawable.ic_baseline_arrow_back_24)
            assistantButtons.forEach {
                it.visibility = View.GONE
            }
        }
    }

    private fun findNewFaces(delayMs: Long) {
        if (!isDetecting) {
            boundingBoxes.forEach {
                removeViewFromWM(it)
            }
            detectFacesJob.cancel()
            detectFacesJob = CoroutineScope(IO).launch {
                Log.d(TAG, "detectFacesJob: called")
                isDetecting = true
                delay(delayMs)
                val screenshotBtm = takeScreenshot()
                if (screenshotBtm != null) {
                    processBitmap(screenshotBtm)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        val detectedClothes = detectClothes(screenshotBtm)
//                        withContext(Main) {
//                            detectedClothes.forEach { rectF ->
//                                val boundingBoxLayout = FrameLayout(context)
//                                boundingBoxLayout.setBackgroundResource(R.drawable.bounding_box_drawable);
//                                val params = WindowManager.LayoutParams(
//                                    rectF.width().toInt(),
//                                    rectF.height().toInt(),
//                                    rectF.centerX().toInt(),
//                                    rectF.centerY().toInt(),
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//                                    } else {
//                                        WindowManager.LayoutParams.TYPE_PHONE
//                                    },
//                                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
//                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH ,
//                                    PixelFormat.TRANSLUCENT
//                                )
////                addViewToWM(boundingBoxLayout, params)
//                    }
//
//
//                        }
//                    }
                }
                isDetecting = false
            }
            detectFacesJob.invokeOnCompletion {
                Log.d(TAG, "invokeOnComlition: called")
            }
        }
    }

    private fun takeScreenshot(): Bitmap? {
        val image = imageReader.acquireNextImage()
        if (image == null) return null
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width
        val bmp = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        bmp.copyPixelsFromBuffer(buffer)
        val newBitmap = Bitmap.createBitmap(bmp, 0, 0, image.width, image.height)
        image.close()
        screenshot = newBitmap
        return newBitmap
    }

    private fun processBitmap(bitmap: Bitmap){
        Log.d(TAG, "processBitmap: called, bitmap: $bitmap")
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        faceDetection.process(inputImage)
            .addOnSuccessListener { faces ->
                Log.d(TAG, "processBitmap: success, calling celebrity service api")
                clearBoundingBoxes()
                detectedFaces.addAll(faces)
                if (isAssistantShown){
                    notificationPointer.visibility = View.GONE
                    val facesBitmapList = ArrayList<Bitmap>()
                    detectedFaces.forEach {
                        addBoundingBox(it.boundingBox)
                        facesBitmapList.add(bitmap.cropByBoundingBox(it.boundingBox))
                    }
//                    CoroutineScope(IO).launch {
//                        repository.findCelebrity(facesBitmapList)
//                    }
                } else {
                    if (faces.isNotEmpty()){
                        notificationPointer.visibility = View.VISIBLE
                    } else {
                        notificationPointer.visibility = View.GONE
                    }
                }
            }
            .addOnCompleteListener {
                isDetecting = false
                Log.d(TAG, "processBitmap: completed")
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addBoundingBox(rect: Rect) {
        val boundingBoxLayout = layoutInflater.inflate(R.layout.bounding_box_layout, null)
        val frame = boundingBoxLayout.findViewById<FrameLayout>(R.id.bounding_box)
        val celebName = frame.findViewById<TextView>(R.id.celeb_name_text_view)
        val circle = Button(context)
        circle.setBackgroundResource(R.drawable.gray_circle_shape_filled)
        circle.alpha = 0.6f
        val progressBar = ProgressBar(context)
        progressBar.indeterminateTintList = ColorStateList.valueOf(Color.GRAY)
        val circleParams = FrameLayout.LayoutParams(
            rect.height() / 6,
            rect.height() / 6
        ).apply {
            gravity = Gravity.CENTER
        }
        frame.addView(circle, circleParams)
        circle.setOnClickListener { circle: View ->
            Log.d(TAG, "addBoundingBox: clicked!")
            screenshot?.let {
                CoroutineScope(Main).launch {
                    frame.removeView(circle)
                    frame.addView(progressBar, circleParams)
                    Log.d(TAG, "addBoundingBox: searching for celeb")
                    withContext(IO) {
                        repository.findCelebrity(listOf(compressInputImage(it.cropByBoundingBox(rect))))[0]?.let{ name ->
                            withContext(Main) {
                                rect.showNameOfCeleb(name)
                            }
                        }
                    }
                    Log.d(TAG, "addBoundingBox: search ended")
                }
            }
        }
        val params = WindowManager.LayoutParams(
            rect.height(),
            rect.height(),
            rect.centerX() - widthPx/2,
            (rect.centerY() * 1.05).toInt() - heightPx/2,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH ,
            PixelFormat.TRANSLUCENT
        )
        boundingBoxes.add(boundingBoxLayout)
        faceCircles.add(circle)
        addViewToWM(boundingBoxLayout, params)
    }

    fun compressInputImage(bitmap: Bitmap): Bitmap {
        var dpBitmap = Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888)
        val bitmapInputImage = bitmap
        try {
            if (bitmapInputImage.getWidth() > 2048 && bitmapInputImage.getHeight() > 2048) {
                 dpBitmap = Bitmap.createScaledBitmap(bitmapInputImage, 1024, 1280, true)
            } else if (bitmapInputImage.getWidth() > 2048 && bitmapInputImage.getHeight() < 2048) {
                  dpBitmap = Bitmap.createScaledBitmap(bitmapInputImage, 1920, 1200, true)
            } else if (bitmapInputImage.getWidth() < 2048 && bitmapInputImage.getHeight() > 2048) {
                 dpBitmap = Bitmap.createScaledBitmap(bitmapInputImage, 1024, 1280, true)
            } else if (bitmapInputImage.getWidth() < 2048 && bitmapInputImage.getHeight() < 2048) {
                 dpBitmap = Bitmap.createScaledBitmap(
                    bitmapInputImage,
                    bitmapInputImage.getWidth(),
                    bitmapInputImage.getHeight(),
                    true
                )
            }
        } catch (e: Exception) {
            dpBitmap = bitmapInputImage
        }
        return dpBitmap
    }

    private fun getWmLayoutParams(width: Int, height: Int): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            width,
            height,
//        widthPx / 2 - 55,
//        -(heightPx/2) + 55 ,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
    }
    private fun launchApp() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun clearBoundingBoxes() {
        boundingBoxes.forEach {
            removeViewFromWM(it)
        }
        boundingBoxes.clear()
        detectedFaces.clear()
        faceCircles.clear()
    }

    private fun Bitmap.cropByBoundingBox(boundingBox: Rect): Bitmap {
        val x = if (boundingBox.exactCenterX().toInt() - boundingBox.width()/2 >= 0)  {
            boundingBox.exactCenterX().toInt() - boundingBox.width()/2
        } else {
            0
        }
        val y = if (boundingBox.exactCenterY().toInt() - boundingBox.height()/2 >= 0) {
            boundingBox.exactCenterY().toInt() - boundingBox.height()/2
        } else {
            0
        }
        var height = boundingBox.height() * 1.25
        var width = boundingBox.width() * 1.2
//        Log.d(TAG, "cropByBoundingBox: y + height = ${y + height}, bitmap.height = ${this.height}")

        if (y + height >= this.height){
            height = 0.0
            var i = y
            while (i < this.height){
                height++
                i++
            }
        }

        if (x + width >= this.width) {
            width = 0.0
            var i = x
            while (i < this.width) {
                width++
                i++
            }
        }
        Log.d(TAG, "cropByBoundingBox: x: $x")
        Log.d(TAG, "cropByBoundingBox: y: $y")
        return Bitmap.createBitmap(this, x, y, width.toInt(), height.toInt())
    }

    private fun insertCelebToCache(name: String, bitmap: Bitmap) {
        CoroutineScope(IO).launch {
            repository.addScreenshotToDb(
                SavedScreenshot(
                    id = UUID.randomUUID().variant(),
                    image = bitmap,
                    celebName = name
                )
            )
        }
    }

    private fun Rect.showNameOfCeleb(name: String) {
        val nameLayout = layoutInflater.inflate(R.layout.celeb_name_layout, null)
        val celebNameTextView = nameLayout.findViewById<TextView>(R.id.celeb_name_text_view)
        celebNameTextView.text = name

//        val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_fade_out_anim)
//        anim.setAnimationListener(object : Animation.AnimationListener{
//            override fun onAnimationStart(animation: Animation?) {
//
//            }
//
//            override fun onAnimationEnd(animation: Animation?) {
//                if (nameLayout.parent != null) {
//                    (frameTouchListener as FrameLayout).removeView(nameLayout)
//                }
//            }
//
//            override fun onAnimationRepeat(animation: Animation?) {
//
//            }
//
//        })
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
        )

        params.topMargin = top + height() / 2 + 35
        params.marginStart = left

        (frameTouchListener as FrameLayout).addView(nameLayout, params)

//        nameLayout.startAnimation(anim)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun detectClothes(bitmap: Bitmap): List<RectF> {
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
//            rects.forEach {rectF ->
//                addBoundingBox(rectF.toRect())
//                val boundingBoxLayout = FrameLayout(context)
//                boundingBoxLayout.setBackgroundResource(R.drawable.bounding_box_drawable);
//                val params = WindowManager.LayoutParams(
//                    rectF.width().toInt(),
//                    rectF.height().toInt(),
//                    rectF.centerX().toInt(),
//                    rectF.centerY().toInt(),
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//                    } else {
//                        WindowManager.LayoutParams.TYPE_PHONE
//                    },
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
//                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH ,
//                    PixelFormat.TRANSLUCENT
//                )
//                addViewToWM(boundingBoxLayout, params)
//            }
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





















