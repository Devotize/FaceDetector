package com.sychev.facedetector.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
import com.sychev.facedetector.ui.items.callbacks.SwipeToDeleteCallback
import com.sychev.facedetector.utils.TAG
import com.xwray.groupie.GroupieAdapter
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
    private val notificationPointer = rootView.findViewById<Button>(R.id.notifiaction_point).apply {
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

    private val rootParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
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
    ).apply {
        gravity = Gravity.TOP or Gravity.END
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
    private val boundingBoxes = ArrayList<FrameLayout>()


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
        addViewToWM(rootView, rootParams)
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
            boundingBoxes.clear()
            detectFacesJob.cancel()
            val loadingItem = MessageItem(message = "Looking for faces...", )
            detectFacesJob = CoroutineScope(IO).launch {
                Log.d(TAG, "detectFacesJob: called")
                isDetecting = true
                delay(delayMs)
                addLoadingMessage(loadingItem).join()
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
        val planes: Array<Image.Plane> = image.planes
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
                detectedFaces.clear()
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
                removeLoadingMessage(loadingItem)
                Log.d(TAG, "processBitmap: completed")
            }
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun addBoundingBox(rect: Rect) {
        val frame = FrameLayout(context)
        frame.background = ContextCompat.getDrawable(context, R.drawable.blue_circle_shape)
        frame.setOnClickListener {
            Log.d(TAG, "addBoundingBox: clicked!")
            CoroutineScope(Dispatchers.Main).launch {
                findCelebrity()
            }
        }
        val params = WindowManager.LayoutParams(
            rect.height() / 2,
            rect.height() / 2,
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
        boundingBoxes.add(frame)
        addViewToWM(frame, params)
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

    private fun addLoadingMessage(loadingItem: MessageItem): Job {
        return CoroutineScope(Main + detectFacesJob).launch {
            adapter.add(loadingItem)
        }
    }
    private fun removeLoadingMessage(loadingItem: MessageItem) {
            val position = adapter.getAdapterPosition(loadingItem)
            Log.d(TAG, "removeLoadingMessage: position: $position")
            if (position >= 0) {
                adapter.remove(loadingItem)
                adapter.notifyItemRemoved(position)
            }

    }

}





















