package com.sychev.facedetector.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Point
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.*
import android.media.projection.MediaProjection
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Person
import com.sychev.facedetector.ui.decorators.MessageItemDecoration
import com.sychev.facedetector.ui.items.MessageItem
import com.sychev.facedetector.utils.TAG
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.File
import java.io.IOException
import java.lang.Exception

class VideoFaceDetector(
    private val context: Context,
    private val mediaProjection: MediaProjection
) {
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
    @SuppressLint("ClickableViewAccessibility")
    private val rootView = layoutInflater.inflate(R.layout.face_detector_layout, null).apply {
        setOnTouchListener { v, event ->
            Log.d(TAG, "event: ${event.action}")
            boundingBoxes.forEach {
                removeViewFromWM(it)
            }
            boundingBoxes.clear()
            CoroutineScope(Main).launch {
                if (!isRecorderStarted){
                    delay(1000)
                    startDetectingJob()
                }
            }
            false
        }
    }
    private val adapter = GroupieAdapter()
    private val rv = rootView.findViewById<RecyclerView>(R.id.face_detector_recycler_view).apply {
        adapter = this@VideoFaceDetector.adapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(MessageItemDecoration())
        val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                this@VideoFaceDetector.adapter.removeGroupAtAdapterPosition(viewHolder.adapterPosition)
                this@VideoFaceDetector.adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        })
        itemTouchHelper.attachToRecyclerView(this)
    }
    private val container = rootView.findViewById<FrameLayout>(R.id.container)
    private val layoutParams = WindowManager.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
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

    private val detector = FaceDetector.Builder(context)
        .setProminentFaceOnly(true)
        .build()
    private val absolutePath = context.externalCacheDir?.absolutePath
    private val recordName = "/record1.mp4"
    private val recordFile = File(absolutePath + recordName)
    private var mediaRecorder: MediaRecorder? = null
    private val faceDetection = FaceDetection.getClient()
    private var isRecording = false
    private var isRecorderStarted = false
    private val job = Job().apply {
        invokeOnCompletion {
            Log.d(TAG, "invokeJobOnComplition called")
            isRecording = false
        }
    }
    private val boundingBoxes = ArrayList<FrameLayout>()


    init {
        open()

        createCaptureSession()
    }

    private fun configureMediaRecorder() {
        if (mediaRecorder == null){
            mediaRecorder = MediaRecorder().apply {
                val profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW)
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setVideoEncodingBitRate(profile.videoBitRate)
                setVideoFrameRate(profile.videoFrameRate)
                setVideoSize(widthPx, heightPx)
                setOutputFile(recordFile.absolutePath)

                try {
                    prepare()
                } catch (e: IOException) {
                    throw RuntimeException("Prepare failed: ", e)
                }
            }
        }

    }

    fun open() {
        if (rootView.parent == null) {
            Log.d(TAG, "open: called")
            windowManager.addView(rootView, layoutParams)
        }
    }

    fun close() {
        if (rootView.parent != null) {
            windowManager.removeView(rootView)
        }
    }

    private fun createCaptureSession() {
        CoroutineScope(IO + job).launch {
            startDetectingJob()
        }
    }

    private suspend fun getFrameFromRecordFile(): Frame? {
        val mediaRetriever = MediaMetadataRetriever()
        mediaRetriever.setDataSource(absolutePath + recordName)
        mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.let { timeMs ->
                val totalVideoTime = (timeMs.toInt() * 1000).toLong() // total video timne in us
                val bitmap = mediaRetriever.getFrameAtTime(
                    totalVideoTime,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                bitmap?.let {
                    return Frame.Builder().setBitmap(it).build()
                }
            }
        return null
    }

    private suspend fun getBitmapFromRecordFile(): Bitmap? {
        val mediaRetriever = MediaMetadataRetriever()
        mediaRetriever.setDataSource(recordFile.absolutePath)
        Log.d(TAG, "getBitmapFromRecord: ${mediaRetriever.getFrameAtTime((100 * 1000).toLong())}")
        val timeMs = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.let { timeMs ->
                val totalVideoTime = (timeMs.toInt() * 1000).toLong() // total video timne in us
                val bitmap = mediaRetriever.getFrameAtTime(
                    totalVideoTime,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                bitmap?.let {
                    return it
                }
            }
        return null
    }

    private suspend fun startDetectingLoop() {
//        mediaRecorder.maxAmplitude always = 0 on emulator
        delay(100)
        mediaRecorder?.let{ mr->
//            Log.d(TAG, "startDetectingLoop: maxAmplitude: ${mr.maxAmplitude}")
            if (isRecording) {
//                Log.d(TAG, "logMaxAmplitude: greater then 0")
                isRecording = false
                stopMediaRecorder()
                val screenBitmap = getBitmapFromRecordFile()
                if (screenBitmap != null){
                    processBitmap(screenBitmap)
                } else {
                    startDetectingJob()
                }
            }
        }

//        Log.d(TAG, "startDetectingLoop: maxAmplitude = ${mediaRecorder!!.maxAmplitude}")
        startDetectingLoop()
    }

    private suspend fun startMediaRecorder() {
        try {
            mediaRecorder?.start()
            isRecorderStarted = true
            delay(1000)
            isRecording = true
        }catch (e: Exception){
            throw e
        }
    }

    private fun stopMediaRecorder() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecorderStarted = false
            isRecording = false
        }catch (e: Exception) {
            recordFile.delete()
        }
    }

    private suspend fun startDetectingJob() {
        Log.d(TAG, "startDetectingJob: called")
        configureMediaRecorder()
        createVirtualDisplay()
        startMediaRecorder()
        startDetectingLoop()
//        val frame = getFrame()
//        Log.d(TAG, "startDetectingJob: $frame")
//        frame?.let{
//            val faces = detector.detect(it)
//            for (face: Face in faces.valueIterator()) {
//                val position = face.position
//                Log.d(TAG, "startDetectingJob: $position")
//            }
//        }

    }

    private suspend fun processBitmap(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        faceDetection.process(inputImage)
            .addOnSuccessListener { faces ->
//                container.removeAllViews()
//                boundingBoxes.forEach {
//                    removeViewFromWM(it)
//                }
//                boundingBoxes.clear()
                for (face in faces) {
                    addBoundingBox(face.boundingBox)
                }
            }
            .addOnCompleteListener {
                Log.d(TAG, "processBitmap: completed")
                CoroutineScope(IO).launch {
//                    startDetectingJob()
                }
            }
    }

    private fun createVirtualDisplay() {
        val flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
        mediaProjection.createVirtualDisplay(
            "screen_display",
            widthPx,
            heightPx,
            context.resources.displayMetrics.densityDpi,
            flags,
            mediaRecorder?.surface,
            null,
            null
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addBoundingBox(rect: Rect) {
//        Log.d(TAG, "addBoundingBox: called")
        val frame = FrameLayout(context)
        frame.background = ContextCompat.getDrawable(context, R.drawable.bounding_box_drawable)
//        frame.background = ColorDrawable(ContextCompat.getColor(context, R.color.black))
//        val layoutParams = FrameLayout.LayoutParams(
//            rect.width(),
//            rect.height()
//        )
//        layoutParams.leftMargin = rect.left
//        layoutParams.topMargin = rect.top
        frame.setOnClickListener {
            Log.d(TAG, "addBoundingBox: clicked!")
            CoroutineScope(Dispatchers.Main).launch {
                findCelebrity()
            }
        }
        val params = WindowManager.LayoutParams(
            rect.width(),
            rect.height(),
            rect.centerX() - widthPx/2,
            rect.centerY() - heightPx/2,
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
//        container.addView(frame, layoutParams)
    }

    private fun addViewToWM(view: View, params: WindowManager.LayoutParams) {
        if (view.parent == null) {
            windowManager.addView(view, params)
        }
    }

    private fun removeViewFromWM(view: View) {
        if (view.parent != null) {
            windowManager.removeView(view)
        }
    }

    private suspend fun findCelebrity() {
        val item = MessageItem()
        adapter.add(item)
        delay(2000)
        item.setPerson(Person("Brad Pitt"))
        item.notifyChanged()
        adapter.notifyDataSetChanged()
    }

}



















