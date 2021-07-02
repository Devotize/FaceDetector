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
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

import com.sychev.facedetector.presentation.MainActivity
import com.sychev.facedetector.presentation.ui.items.BottomFavoriteSheet
import com.sychev.facedetector.presentation.ui.items.BottomGallerySheet
import com.sychev.facedetector.presentation.ui.items.DetectedClothesListItem
import com.sychev.facedetector.presentation.ui.items.SnackbarItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PhotoDetector
    (
    private val context: Context,
    private val mediaProjection: MediaProjection,
    private val stopService: (() -> Unit)? = null,
) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PhotoDetectorEntryPoint{
        fun provideRepository(): SavedScreenshotRepo
        fun provideViewModel(): DetectorViewModel
    }



    private val entryPoint = EntryPointAccessors.fromApplication(context, PhotoDetectorEntryPoint::class.java)
    private val viewModel = entryPoint.provideViewModel()
    private val repository = entryPoint.provideRepository()
    private val detectedClothesList = DetectedClothesListItem(context)
    private val bottomGallerySheet = BottomGallerySheet(context)
    private val bottomFavoriteSheet = BottomFavoriteSheet(context)

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
    private val rootView = layoutInflater.inflate(R.layout.detector_assistant_layout, null)
    private val detectorButtonsLayout = rootView.findViewById<ConstraintLayout>(R.id.detector_buttons_layout)
    private val showButton = rootView.findViewById<FrameLayout>(R.id.detector_show_button).apply {
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

    private val messageButton = rootView.findViewById<ImageButton>(R.id.detector_message_button).apply {
        setOnClickListener {
            viewModel.onSelectedButtonChange(SelectedButton.MessageButton)
        }
    }

    private val cameraButton = rootView.findViewById<ImageButton>(R.id.detector_camera_button).apply {
        setOnClickListener {
            viewModel.onSelectedButtonChange(SelectedButton.CameraButton)
            takeScreenshot()?.let {
//                viewModel.onTriggerEvent(DetectorEvent.SearchClothesEvent(it))
                viewModel.onTriggerEvent(DetectorEvent.DetectClothesLocalEvent(it))
            }

        }
    }
    private val progressBarCenter = ProgressBar(context)

    private val galleryButton = rootView.findViewById<ImageButton>(R.id.detector_gallery_button).apply {
        setOnClickListener {
            viewModel.onSelectedButtonChange(SelectedButton.GalleryButton)
            clearBoundingBoxes()
            bottomGallerySheet.open()
        }
    }
    private val closeButton = rootView.findViewById<ImageButton>(R.id.detector_close_button).apply {
        val closeDrawable = GradientDrawable()
        closeDrawable.shape = GradientDrawable.OVAL
        closeDrawable.setColor(ContextCompat.getColor(context, R.color.pink))
        background = closeDrawable

        setOnClickListener {
            viewModel.onSelectedButtonChange(null)
            (frameTouchListener as FrameLayout).removeAllViews()
            isAssistantShown = !isAssistantShown
            showOrHideAssistant()
        }
    }
    private val favoriteButton = rootView.findViewById<ImageButton>(R.id.detector_favorite_button).apply {
        setOnClickListener {
            viewModel.onSelectedButtonChange(SelectedButton.FavoriteButton)
            bottomFavoriteSheet.open()
        }
    }

    private val settingsButton = rootView.findViewById<ImageButton>(R.id.detector_settings_button).apply {
        setOnClickListener {
            viewModel.onSelectedButtonChange(SelectedButton.SettingsButton)
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
        onDetectorCreated()
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
            showButton.visibility = View.INVISIBLE
            notificationPointer.visibility = View.GONE
            detectorButtonsLayout.visibility = View.VISIBLE
        } else {
            sheetArrow.setImageResource(R.drawable.ic_baseline_arrow_back_24)
            showButton.visibility = View.VISIBLE
            detectorButtonsLayout.visibility = View.GONE
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

    private fun showProgressBar() {
        if (progressBarCenter.parent == null) {
            (frameTouchListener as FrameLayout).addView(
                progressBarCenter,
                FrameLayout.LayoutParams(110, 110).apply { gravity = Gravity.CENTER })
        }
    }

    private fun hideProgressBar() {
        if (progressBarCenter.parent != null){
            (frameTouchListener as FrameLayout).removeView(progressBarCenter)
        }
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



    private fun onDetectorCreated() {
        viewModel.loading.onEach { loading ->
            if (loading) {
                showProgressBar()
                frameTouchListener.background = ColorDrawable(ContextCompat.getColor(context, R.color.black_transparent))
            } else {
                hideProgressBar()
                frameTouchListener.background = ColorDrawable(ContextCompat.getColor(context, R.color.transparent))
            }
        }.launchIn(CoroutineScope(Main))

        viewModel.detectedClothesList.onEach {
            Log.d(TAG, "onDetectorCreated: detectedClothes: $it")
        }.launchIn(CoroutineScope(Main))


        viewModel.selectedButton.onEach { selectedButton ->
            //message button
            val messageDrawable = GradientDrawable()
            messageDrawable.shape = GradientDrawable.OVAL
            messageDrawable.setColor(ContextCompat.getColor(context, R.color.purple))
            if (selectedButton == SelectedButton.MessageButton) {
                messageDrawable.setStroke(2, ContextCompat.getColor(context, R.color.white))
            }else
                messageDrawable.setStroke(0, 0)
            messageButton.background = messageDrawable
            //gallery button
            val galleryDrawable = GradientDrawable()
            galleryDrawable.shape = GradientDrawable.OVAL
            galleryDrawable.setColor(ContextCompat.getColor(context, R.color.yellow))
            if (selectedButton == SelectedButton.GalleryButton) {
                galleryDrawable.setStroke(2, ContextCompat.getColor(context, R.color.white))
            }else
                galleryDrawable.setStroke(0, 0)
            galleryButton.background = galleryDrawable
            // camera button
            val cameraDrawable = GradientDrawable()
            cameraDrawable.shape = GradientDrawable.OVAL
            cameraDrawable.setColor(ContextCompat.getColor(context, R.color.orange))
            if (selectedButton == SelectedButton.CameraButton) {
                cameraDrawable.setStroke(2, ContextCompat.getColor(context, R.color.white))
            } else {
                cameraDrawable.setStroke(0,0)
            }
            cameraButton.background = cameraDrawable
            //favorite button
            val favoriteDrawable = GradientDrawable()
            favoriteDrawable.shape = GradientDrawable.OVAL
            favoriteDrawable.setColor(ContextCompat.getColor(context, R.color.blue))
            if (selectedButton == SelectedButton.FavoriteButton) {
                favoriteDrawable.setStroke(2, ContextCompat.getColor(context, R.color.white))
            } else {
                favoriteDrawable.setStroke(0,0)
            }
            favoriteButton.background = favoriteDrawable
            //settings button
            val settingsDrawable = GradientDrawable()
            settingsDrawable.shape = GradientDrawable.OVAL
            settingsDrawable.setColor(ContextCompat.getColor(context, R.color.sky_blue))
            if (selectedButton == SelectedButton.SettingsButton) {
                settingsDrawable.setStroke(2, ContextCompat.getColor(context, R.color.white))
            } else {
                settingsDrawable.setStroke(0,0)
            }
            settingsButton.background = settingsDrawable

        }.launchIn(CoroutineScope(Main))

    }

}





















