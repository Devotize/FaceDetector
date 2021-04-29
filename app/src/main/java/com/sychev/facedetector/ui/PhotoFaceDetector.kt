package com.sychev.facedetector.ui

import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Person
import com.sychev.facedetector.ui.decorators.MessageItemDecoration
import com.sychev.facedetector.ui.items.MessageItem
import com.sychev.facedetector.ui.items.SaveScreenshotItem
import com.sychev.facedetector.ui.items.callbacks.SwipeToDeleteCallback
import com.sychev.facedetector.utils.TAG
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class PhotoFaceDetector(
    private val context: Context,
    private val mediaProjection: MediaProjection
) {
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
    private val icon = rootView.findViewById<ImageButton>(R.id.icon)
    private val assistantFrame = rootView.findViewById<ConstraintLayout>(R.id.assistant_frame)
    private val adapter: GroupieAdapter = GroupieAdapter()
    private val rv = rootView.findViewById<RecyclerView>(R.id.face_detector_recycler_view).apply {
        adapter = this@PhotoFaceDetector.adapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(MessageItemDecoration())
        val onSwipeCallback = object : SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d(TAG, "onSwiped: swiped")
                val position = viewHolder.adapterPosition
                this@PhotoFaceDetector.adapter.remove(this@PhotoFaceDetector.adapter.getGroupAtAdapterPosition(position))
                this@PhotoFaceDetector.adapter.notifyItemRemoved(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(onSwipeCallback)
        itemTouchHelper.attachToRecyclerView(this)
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
    private val saveScreenshotItem = SaveScreenshotItem()


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
    }

    private fun showOrHideAssistant(){
        if (isAssistantShown){
            assistantFrame.visibility = View.VISIBLE
            sheetArrow.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
            notificationPointer.visibility = View.GONE
        } else {
            assistantFrame.visibility = View.GONE
            sheetArrow.setImageResource(R.drawable.ic_baseline_arrow_back_24)
        }
    }

    private fun findNewFaces(delayMs: Long) {
        if (!isDetecting) {
            boundingBoxes.forEach {
                removeViewFromWM(it)
            }
            removeMessage(saveScreenshotItem)
            faceCircles.clear()
            boundingBoxes.clear()
            detectFacesJob.cancel()
            val loadingItem = MessageItem(message = "Looking for faces...", )
            detectFacesJob = CoroutineScope(IO).launch {
                Log.d(TAG, "detectFacesJob: called")
                isDetecting = true
                delay(delayMs)
                withContext(Main){
                    addMessage(loadingItem)
                }
                val screenshotBtm = takeScreenshot()
                processBitmap(screenshotBtm, loadingItem)
                isDetecting = false
            }
            detectFacesJob.invokeOnCompletion {
                Log.d(TAG, "invokeOnComlition: called")
            }
        }
    }

    private fun takeScreenshot(): Bitmap {
        val image = imageReader.acquireNextImage()
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
        return newBitmap
    }

    private fun processBitmap(bitmap: Bitmap, loadingItem: MessageItem){
        Log.d(TAG, "processBitmap: called, bitmap: $bitmap")
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        faceDetection.process(inputImage)
            .addOnSuccessListener { faces ->
                Log.d(TAG, "processBitmap: success")
                removeMessage(saveScreenshotItem)
                detectedFaces.clear()
                detectedFaces.addAll(faces)
                if (isAssistantShown){
                    saveScreenshotItem.onClick = {
                        showScreenshotAnim(bitmap)
                    }
                    addMessage(saveScreenshotItem)
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
                removeMessage(loadingItem)
                Log.d(TAG, "processBitmap: completed")
            }
    }

    private fun addMessage(messageItem: Item<GroupieViewHolder>) {
        adapter.add(messageItem)
        val position = adapter.getAdapterPosition(messageItem)
        adapter.notifyItemInserted(position)
    }

    private fun removeMessage(messageItem: Item<GroupieViewHolder>) {
        val position = adapter.getAdapterPosition(messageItem)
        if (position >= 0) {
            adapter.remove(messageItem)
            adapter.notifyItemRemoved(position)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addBoundingBox(rect: Rect) {
        val boundingBoxLayout = layoutInflater.inflate(R.layout.bounding_box_layout, null)
        val frame = boundingBoxLayout.findViewById<FrameLayout>(R.id.bounding_box)
        val circle = Button(context)
        circle.setBackgroundResource(R.drawable.blue_circle_shape)
        val circleParams = FrameLayout.LayoutParams(
            rect.height() / 2,
            rect.height() / 2
        ).apply {
            gravity = Gravity.CENTER
        }
        frame.addView(circle, circleParams)
        circle.setOnClickListener {
            Log.d(TAG, "addBoundingBox: clicked!")
            CoroutineScope(Dispatchers.Main).launch {
                findCelebrity()
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
        animateCircle()
    }

    private fun animateCircle() {
        if (faceCircles.isNotEmpty()){
            val rand = (0 until faceCircles.size).random()
            val anim = AnimationUtils.loadAnimation(context, R.anim.expand_anim)
            anim.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    animateCircle()
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
            faceCircles[rand].startAnimation(anim)
        }
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

    private fun showScreenshotAnim(bitmap: Bitmap) {
        val frameLayout = FrameLayout(context)
        val imageView = ImageView(context)
        imageView.setImageBitmap(bitmap)
        frameLayout.addView(
            imageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            )
        )
        addViewToWM(frameLayout, getWmLayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,)
        )
        val screenshotAnim = AnimationUtils.loadAnimation(context, R.anim.saving_screenshot_anim)
        screenshotAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                Log.d(TAG, "onAnimationEnd: AnimationEnded")
                removeViewFromWM(frameLayout)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        imageView.animation = screenshotAnim
        screenshotAnim.start()
    }

    private suspend fun findCelebrity() {
        val item = MessageItem(message = "I think this is...")
        adapter.add(item)
        delay(2500)
        val person = Person(
            name = "Brad Pitt",
            googleSearch = "https://www.google.com/search?q=brad+pitt",
            instUrl = "https://www.instagram.com/bradpittofflcial/?hl=en",
            facebookUrl = "https://www.facebook.com/Brad-Pitt-165952813475830/",
            kinopoiskUrl = "https://www.kinopoisk.ru/name/25584/"
        )
        item.setPerson(person)
        item.setMessage("I think this is")
        adapter.notifyDataSetChanged()
    }

}





















