package com.sychev.facedetector.presentation.ui.assistant_items

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.*
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.sychev.facedetector.R
import com.sychev.facedetector.presentation.custom_view.ResizableRectangleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ResizableBoundingBoxItem(
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

    private var onDoneAction: (Rect) -> Unit = {}

    private val doneButton = ImageButton(context).apply {
        background = ContextCompat.getDrawable(context, R.drawable.white_circle_shape_filled)
        setImageResource(R.drawable.ic_baseline_done_24_grey)
        setOnClickListener {
            onDoneAction(rect)
            hide()
        }
    }
    private val cancelButton = ImageButton(context).apply {
        background = ContextCompat.getDrawable(context, R.drawable.white_circle_shape_filled)
        setImageResource(R.drawable.ic_baseline_close_gray)
        setOnClickListener {
            hide()
        }
    }

    private val rootView = ResizableRectangleView(context)
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
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        ,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.CENTER
    }

    val left
        get() = rootView.rectLeft
    val top
        get() = rootView.rectTop
    val right
        get() = rootView.rectRight
    val bottom
        get() = rootView.rectBottom

    private var rect = Rect()

    var isShowing = false

    fun setLayoutParams(params: WindowManager.LayoutParams) {
        layoutParams = params
    }

    fun show(xCoordinates: List<Float>, yCoordinates: List<Float>) {
        if (rootView.parent == null) {
            isShowing = true
            val buttonWidth = 50
            val buttonHeight = 50
            rootView.setCoordinates(xCoordinates = xCoordinates, yCoordinates = yCoordinates)
            windowManager.addView(rootView, layoutParams)
            windowManager.addView(doneButton, layoutParams.apply {
                width = buttonWidth
                height = buttonHeight
                x = (-widthPx / 2) + rootView.rectLeft + ((rootView.rectRight - rootView.rectLeft) / 3)
                y = (-heightPx / 2) + (-heightPx / 2) + rootView.rectBottom + 60
            })
            windowManager.addView(cancelButton, layoutParams.apply {
                width = buttonWidth
                height = buttonHeight
                x = (-widthPx / 2) + rootView.rectLeft + ((rootView.rectRight - rootView.rectLeft) / 3) + buttonWidth + 20
                y = (-heightPx / 2) + (-heightPx / 2) + rootView.rectBottom + 60
            })

            rootView.boxState.onEach {
//                Log.d(TAG, "show: $it")
                rect = it
                if (doneButton.parent != null){
                    try {
                        windowManager.updateViewLayout(doneButton,layoutParams.apply {
                            x = (-widthPx / 2) + it.left + ((it.right - it.left) / 3)
                            y = (-heightPx / 2) + it.bottom + 60
                            gravity = Gravity.CENTER
                        })
                        windowManager.updateViewLayout(cancelButton,layoutParams.apply {
                            x = (-widthPx / 2) + it.left + ((it.right - it.left) / 3) + buttonWidth + 20
                            y = (-heightPx / 2) + it.bottom + 60
                            gravity = Gravity.CENTER
                        })
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.launchIn(CoroutineScope(Main))
        }
    }

    fun hide() {
        if (rootView.parent != null) {
            isShowing = false
            windowManager.removeView(rootView)
            if (cancelButton.parent != null && doneButton.parent != null) {
                windowManager.removeView(cancelButton)
                windowManager.removeView(doneButton)
            }
        }
    }

    fun setOnDoneClick(unit: (Rect) -> Unit) {
        onDoneAction = unit
    }

}