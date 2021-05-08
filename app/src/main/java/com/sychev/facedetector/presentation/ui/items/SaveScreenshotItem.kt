package com.sychev.facedetector.presentation.ui.items

import android.graphics.Bitmap
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sychev.facedetector.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class SaveScreenshotItem(
    var onClick: (() -> Unit)? = null
): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener {
            //add screenshot to db
            onClick?.invoke()
        }
    }

    override fun getLayout() = R.layout.save_screenshot_item
}