package com.sychev.facedetector.presentation.ui.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.Log
import android.view.*
import com.sychev.facedetector.presentation.custom_view.FrameDrawView

class FrameDrawItem(
    private val context: Context,
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

    private var addClothesPointer: (Rect) -> Unit = {}

    @SuppressLint("ClickableViewAccessibility")
    private val rootView = FrameDrawView(context = context).apply {
        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP){
                val boundingBox = ResizableBoundingBoxItem(context)
                boundingBox.show(this.touchedCoordinatesX, this.touchedCoordinatesY)
                boundingBox.setOnDoneClick {
                    addClothesPointer.invoke(it)
                }
//                hide()
//                assistant?.recycleAssistantView()
            }
            false
        }
    }
    private var layoutParams = WindowManager.LayoutParams(
        widthPx - 10,
        heightPx - 10,
        0,
        0,
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


    fun setLayoutParams(params: WindowManager.LayoutParams) {
        layoutParams = params
    }

    fun show() {
        if (rootView.parent == null){
            windowManager.addView(rootView, layoutParams)
        }
    }

    fun hide() {
        if (rootView.parent != null) {
            windowManager.removeView(rootView)
        }
    }

    fun cropBitmap(
        bitmap: Bitmap,
        width: Int,
        height: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): Bitmap {
        val croppedBtm = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(croppedBtm)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val srcRect = Rect(left, top, right, bottom)
        val destRect =
            Rect(left, top, right, bottom)
        canvas.drawBitmap(bitmap, srcRect, destRect, Paint())
        return croppedBtm
    }

    fun setAddClothesPointer(unit: (Rect) -> Unit) {
        addClothesPointer = unit
    }

}