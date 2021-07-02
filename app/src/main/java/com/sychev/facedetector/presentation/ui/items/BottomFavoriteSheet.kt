package com.sychev.facedetector.presentation.ui.items

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sychev.facedetector.R
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorEvent
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorViewModel
import com.sychev.facedetector.presentation.ui.items.adapter.BottomGalleryListAdapter
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BottomFavoriteSheet(
    private val context: Context
): BottomSheetBehavior.BottomSheetCallback() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BottomFavoriteSheetEntryPoint{
        fun providesViewModel(): DetectorViewModel
    }
    private val entryPoint = EntryPointAccessors.fromApplication(context, BottomFavoriteSheetEntryPoint::class.java)
    private val viewModel = entryPoint.providesViewModel()

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainLayout: CoordinatorLayout = layoutInflater.inflate(R.layout.bottom_favorite_clothes_sheet_layout, null).apply {
        setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    } as CoordinatorLayout
    private val bottomSheet = mainLayout.findViewById<CardView>(R.id.bottom_favorite_clothes_sheet)
    private val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
    private val myAdapter = BottomGalleryListAdapter(ArrayList(), viewModel)
    private val myRecyclerView = mainLayout.findViewById<RecyclerView>(R.id.bottom_favorite_clothes_recycler_view).apply {
        adapter = myAdapter
        layoutManager = GridLayoutManager(context, 3)
    }
    private val shareButton = mainLayout.findViewById<Button>(R.id.bottom_favorite_clothes_share_button).apply {
        setOnClickListener {
            viewModel.onSelectorModeChanged(true)
        }
    }
    private val doneButton = mainLayout.findViewById<Button>(R.id.bottom_favorite_clothes_done_button).apply {
        setOnClickListener {
            viewModel.onTriggerEvent(DetectorEvent.ShareMultiplyUrls(myAdapter.urlsToShare))
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }
    private val cancelButton = mainLayout.findViewById<Button>(R.id.bottom_favorite_clothes_cancel_button).apply {
        setOnClickListener {
            viewModel.onSelectorModeChanged(false)
        }
    }
    private val clearButton = mainLayout.findViewById<Button>(R.id.bottom_favorite_clothes_clear_button)

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
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

    init {
        sheetBehavior.addBottomSheetCallback(this)
        sheetBehavior.isHideable = true
    }

    fun open() {
        if (mainLayout.parent == null) {
            windowManager.addView(mainLayout, layoutParams)
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        viewModel.onTriggerEvent(DetectorEvent.GetFavoriteClothesEvent)
        viewModel.favoriteClothesList.onEach { detectedClothesList ->
            myAdapter.list.clear()
            myAdapter.list.addAll(detectedClothesList)
            myAdapter.notifyDataSetChanged()
        }.launchIn(CoroutineScope(Main))
        viewModel.isSelectorMod.onEach { isSelectorMode ->
            if (isSelectorMode) {
                doneButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE
                shareButton.visibility = View.GONE
            } else {
                doneButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
                shareButton.visibility = View.VISIBLE
            }
        }.launchIn(CoroutineScope(Main))

    }

    fun close() {
        if (mainLayout.parent != null) {
            windowManager.removeView(mainLayout)
            viewModel.onSelectorModeChanged(false)
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState){
            BottomSheetBehavior.STATE_HIDDEN -> {
                close()
            }
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
    }

}