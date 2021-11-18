package com.sychev.facedetector.presentation.ui.assistant_items

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sychev.facedetector.R
import com.sychev.facedetector.utils.TAG
import java.util.*

class SnackbarItem(
    private val context: Context
): BottomSheetBehavior.BottomSheetCallback() {

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val rootView = layoutInflater.inflate(R.layout.snackbar_item_layout, null)
    private val textView = rootView.findViewById<TextView>(R.id.snackbar_item_text_view)
    private val sheetBar = rootView.findViewById<FrameLayout>(R.id.snackbar_item_bottom_sheet)
    private val sheetBehavior = BottomSheetBehavior.from(sheetBar)

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
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
        gravity = Gravity.BOTTOM
    }


    fun open(message: String) {
        Log.d(TAG, "open: called")
        if (rootView.parent == null) {
            windowManager.addView(rootView, layoutParams)
            sheetBehavior.addBottomSheetCallback(this)
            sheetBehavior.isHideable = true
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            textView.text = message
            changeStateWithDelay(BottomSheetBehavior.STATE_HIDDEN, 3000)
        }
    }

    fun close() {
        if (rootView.parent != null) {
            windowManager.removeView(rootView)
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_HIDDEN -> {
                close()
            }
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {

    }

    private fun changeStateWithDelay(newState: Int, delay: Long) {
        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                sheetBehavior.state = newState
                timer.cancel()
            }
        }, delay)
    }

}