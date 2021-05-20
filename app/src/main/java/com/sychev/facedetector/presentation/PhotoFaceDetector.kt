package com.sychev.facedetector.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Build
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat.startForegroundService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Person
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.presentation.ui.items.MessageItem
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.service.FaceDetectorService
import com.sychev.facedetector.utils.TAG
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.*
import kotlin.collections.ArrayList


class PhotoFaceDetector
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
            findNewFaces(0)
        }
    }
    private val assistantButtons: ArrayList<ImageButton> = ArrayList()
    private val openAppButton = rootView.findViewById<ImageButton>(R.id.app_button).apply {
        assistantButtons.add(this)
        setOnClickListener {
            launchApp()
            clearBoundingBoxes()
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
            findNewFaces(2000)
            
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
                Log.d(TAG, "processBitmap: success")
                clearBoundingBoxes()
                detectedFaces.addAll(faces)
                if (isAssistantShown){
                    for (face in detectedFaces) {
                        addBoundingBox(face.boundingBox)
                        notificationPointer.visibility = View.GONE
                    }
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
        circle.setBackgroundResource(R.drawable.red_circle_shape_filled)
        circle.alpha = 0.4f
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
                circleParams.width = rect.height() / 6
                circleParams.height = rect.height() / 6
                circle.setBackgroundResource(R.drawable.red_circle_shape)
                frame.requestLayout()
                animateCircle(circle)
//                animateCircle()
                CoroutineScope(Dispatchers.Main).launch {
                    findCelebrity(){ person ->
                        insertCelebToCache(person.name, it.cropByBoundingBox(rect))
//                        animateCircle(circle, false)
                        circleParams.width = rect.height() / 6
                        circleParams.height = rect.height() / 6
                        circle.setBackgroundResource(R.drawable.red_circle_shape_filled)
                        frame.requestLayout()
                        circle.animation.setAnimationListener(object : Animation.AnimationListener{
                            override fun onAnimationStart(animation: Animation?) {

                            }

                            override fun onAnimationEnd(animation: Animation?) {

                            }

                            override fun onAnimationRepeat(animation: Animation?) {
                            }

                        })
                        circle.animation.cancel()
                        circle.animation = null
                        rect.showNameOfCeleb(person.name)
//                        celebName.text = person.name
//                        celebName.visibility = View.VISIBLE
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            celebName.visibility = View.GONE
//                        },3000)

                    }
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
//        animateCircles()
    }
//
//    private fun animateCircles() {
//        if (faceCircles.isNotEmpty()){
//            val rand = (0 until faceCircles.size).random()
//            val anim = AnimationUtils.loadAnimation(context, R.anim.expand_anim)
//            val faceCircle = faceCircles[rand]
//            anim.setAnimationListener(object : Animation.AnimationListener{
//                override fun onAnimationStart(animation: Animation?) {
//                    faceCircle.setBackgroundResource(R.drawable.red_circle_shape)
//                }
//
//                override fun onAnimationEnd(animation: Animation?) {
//                    faceCircle.setBackgroundResource(R.drawable.red_circle_shape_filled)
//                    animateCircles()
//                }
//
//                override fun onAnimationRepeat(animation: Animation?) {
//
//                }
//            })
////            faceCircles[rand].startAnimation(anim)
//            faceCircle.startAnimation(anim)
//        }
//    }

    private fun animateCircle(circle: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.expand_anim)
        anim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                circle.startAnimation(animation)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }


        })
        circle.startAnimation(anim)
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

    private suspend fun findCelebrity(person: (Person) -> Unit) {
        val item = MessageItem(message = "I think this is...")
        delay(3500)
        val person = Person(
            name = "Celeb Name",
            googleSearch = "https://www.google.com/search?q=brad+pitt",
            instUrl = "https://www.instagram.com/bradpittofflcial/?hl=en",
            facebookUrl = "https://www.facebook.com/Brad-Pitt-165952813475830/",
            kinopoiskUrl = "https://www.kinopoisk.ru/name/25584/"
        )
        item.setPerson(person)
        item.setMessage("I think this is")
        person(person)
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
        val x = boundingBox.exactCenterX().toInt() - boundingBox.width()/2
        val y = boundingBox.exactCenterY().toInt() - boundingBox.height()/2
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

        val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_fade_out_anim)
        anim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (nameLayout.parent != null) {
                    (frameTouchListener as FrameLayout).removeView(nameLayout)
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
        )

        params.topMargin = bottom + 30
        params.marginStart = left

        (frameTouchListener as FrameLayout).addView(nameLayout, params)

        nameLayout.startAnimation(anim)

    }

}





















