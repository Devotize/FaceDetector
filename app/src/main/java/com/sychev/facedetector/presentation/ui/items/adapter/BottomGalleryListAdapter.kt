package com.sychev.facedetector.presentation.ui.items.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorEvent
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorViewModel
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BottomGalleryListAdapter(val list: ArrayList<Clothes>, private val viewModel: DetectorViewModel): RecyclerView.Adapter<BottomGalleryListAdapter.MyViewHolder>(){

    val urlsToShare = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.clothes_item_bottom_sheet, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(
        private val itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private val clothesImageView = itemView.findViewById<ImageView>(R.id.clothes_item_bottom_sheet_image_view)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.clothes_item_bottom_sheet_check_box)
        private val closeButton = itemView.findViewById<Button>(R.id.clothes_item_bottom_sheet_close_button)
        fun bind(clothes: Clothes, position: Int) {
            Glide.with(itemView)
                .load(clothes.picUrl)
                .placeholder(R.drawable.clothes_default_icon)
                .into(clothesImageView)

            viewModel.isSelectorMod
                .onEach { isSelectorMode ->
                    if (isSelectorMode) {
                        checkBox.visibility = View.VISIBLE
                    } else  {
                        checkBox.isChecked = false
                        checkBox.visibility = View.GONE
                        urlsToShare.clear()
                    }
                }
                .launchIn(CoroutineScope(Main))

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    urlsToShare.add(clothes.url)
                } else {
                    try {
                        urlsToShare.remove(clothes.url)
                    }catch (e: Exception){
                        e.printStackTrace()
                        Log.d(TAG, "bind: error -> ${e.message}")
                    }
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clothes.url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                it.context.startActivity(
                    intent,
                )
            }

            closeButton.setOnClickListener {
                viewModel.onTriggerEvent(DetectorEvent.DeleteClothesEvent(clothes))
                list.removeAt(position)
                this@BottomGalleryListAdapter.notifyItemRemoved(position)
                this@BottomGalleryListAdapter.notifyItemRangeChanged(position, list.size)
            }
        }


    }


}