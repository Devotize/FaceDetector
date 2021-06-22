package com.sychev.facedetector.presentation.ui.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorEvent
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorViewModel
import com.sychev.facedetector.presentation.ui.items.adapter.DetectedClothesListAdapter
import com.sychev.facedetector.utils.TAG
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("ViewConstructor")
class DetectedClothesListItem(
    private val myContext: Context,
) : FrameLayout(myContext) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DetectedClothesListEntryPoint{
        fun providesViewModel(): DetectorViewModel
    }
    private val entryPoint = EntryPointAccessors.fromApplication(context, DetectedClothesListEntryPoint::class.java)
    private val viewModel = entryPoint.providesViewModel()

    private val myAdapter = DetectedClothesListAdapter(ArrayList(), viewModel)
    private val recyclerView = RecyclerView(context).apply {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = myAdapter
    }
    private val closeButton = ImageButton(context).apply {
        setImageResource(R.drawable.close_icon_small)
        background = ContextCompat.getDrawable(context, R.drawable.blue_circle_shape_filled)
        setOnClickListener {
            close()
        }
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
        this.addView(
            recyclerView,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        )
//        this.addView(
//            closeButton,
//            LayoutParams(55, 55).apply { gravity = Gravity.TOP or Gravity.END }
//        )
        onInitialize()
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

    private fun onInitialize() {
        viewModel.detectedClothesList.onEach { detectedClothesList ->
            Log.d(TAG, "onInitialize: detectedClothesList: $detectedClothesList")
            open()
            if (detectedClothesList.isEmpty()) {
                close()
                return@onEach
            }
            myAdapter.apply {
                val startPosition = list.size
                val itemCount = detectedClothesList.size
                list.addAll(detectedClothesList)
                notifyItemRangeInserted(startPosition, itemCount)
            }
        }.launchIn(CoroutineScope(Main))
    }

}













