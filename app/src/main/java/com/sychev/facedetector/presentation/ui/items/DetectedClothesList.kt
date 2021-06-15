package com.sychev.facedetector.presentation.ui.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.ui.items.adapter.DetectedClothesListAdapter

@SuppressLint("ViewConstructor")
class DetectedClothesList(
    private val myContext: Context,
): FrameLayout(myContext) {

    val myAdapter = DetectedClothesListAdapter(ArrayList())
    private val recyclerView = RecyclerView(context).apply {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = myAdapter
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.MATCH_PARENT,
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
        gravity = Gravity.TOP or Gravity.START
    }


    init {
        this.addView(recyclerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    fun open() {
        if (this.parent == null) {
            windowManager.addView(this, layoutParams)
        }
    }

    fun close() {
        if (this.parent != null) {
            windowManager.removeView(this)
            myAdapter.list.clear()
        }
    }

}














