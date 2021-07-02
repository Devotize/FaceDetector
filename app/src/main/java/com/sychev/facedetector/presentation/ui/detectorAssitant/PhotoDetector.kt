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
import com.sychev.facedetector.R
import com.sychev.facedetector.repository.SavedScreenshotRepo
import com.sychev.facedetector.utils.TAG
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

import com.sychev.facedetector.presentation.MainActivity
import com.sychev.facedetector.presentation.ui.items.BottomFavoriteSheet
import com.sychev.facedetector.presentation.ui.items.BottomGallerySheet
import com.sychev.facedetector.presentation.ui.items.DetectedClothesListItem
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
    private val detectedClothesList = DetectedClothesListItem(context)
    private val bottomGallerySheet = BottomGallerySheet(context)
    private val bottomFavoriteSheet = BottomFavoriteSheet(context)

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
    private val showButton = rootView.findViewById<FrameLayout>(R.id.detector_show_button).apply {
        setOnClickListener {
            viewModel.setIsActive(true)

            (frameTouchListener as FrameLayout).removeAllViews()
//            findNewFaces(0)
        }
    }
    private val detectorImageView = rootView.findViewById<ImageView>(R.id.detector_image_view).apply {
        setOnClickListener {
            viewModel.setIsActive(false)
            takeScreenshot()?.let { btm ->
                viewModel.onTriggerEvent(DetectorEvent.SearchClothesEvent(btm))
            }
        }
    }
    private val additionExpandedHeight = 8
    private val additionExpandedWidth = 30

    private val progressBarCenter = ProgressBar(context)

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
        onDetectorCreated()
    }

    fun open() {
        addViewToWM(frameTouchListener, frameParams)
        addViewToWM(rootView, getWmLayoutParams(
            showButton.minimumWidth + additionExpandedWidth,
            showButton.minimumHeight + additionExpandedHeight).apply {
            gravity = Gravity.CENTER or Gravity.END
        }
        )
    }

    fun close() {
        removeViewFromWM(rootView)
        removeViewFromWM(frameTouchListener)
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

    private fun expandAssistant() {
        val initialHeight = showButton.minimumHeight
        val initialWidth = showButton.minimumWidth
        showButton.alpha = 1f
        detectorImageView.visibility = View.VISIBLE
        val anim = object : Animation(){

            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                showButton.layoutParams.height = (initialHeight + additionExpandedHeight * interpolatedTime).toInt()
                showButton.layoutParams.width = (initialWidth + additionExpandedWidth * interpolatedTime).toInt()
                showButton.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        anim.duration = 500
        showButton.startAnimation(anim)
    }

    private fun collapseAssistant() {
        val initialHeight = showButton.minimumHeight + additionExpandedHeight
        val initialWidth = showButton.minimumWidth + additionExpandedWidth
        showButton.alpha = 0.7f
        detectorImageView.visibility = View.GONE
        val anim = object : Animation(){
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                showButton.layoutParams.height = (initialHeight - additionExpandedHeight * interpolatedTime).toInt()
                showButton.layoutParams.width = (initialWidth - additionExpandedWidth * interpolatedTime).toInt()
                showButton.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        anim.duration = 500
        showButton.startAnimation(anim)
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

        var timer: Timer? = null
        viewModel.isActive
            .onEach { isActive ->
                Log.d(TAG, "onDetectorCreated: isActive = $isActive")
                isActive?.let{
                    if (isActive) {
                        timer = Timer()
                        expandAssistant()
                        timer?.schedule(object : TimerTask(){
                            override fun run() {
                                if (viewModel.isActive.value == true) {
                                    viewModel.setIsActive(false)
                                }
                            }
                        }, 2000)
                    } else {
                        collapseAssistant()
                        timer?.cancel()
                    }
                }
            }.launchIn(CoroutineScope(Main))

    }

}





















