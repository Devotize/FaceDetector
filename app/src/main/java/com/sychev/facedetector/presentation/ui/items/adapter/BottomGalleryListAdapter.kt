package com.sychev.facedetector.presentation.ui.items.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes

class BottomGalleryListAdapter(val list: ArrayList<DetectedClothes>): RecyclerView.Adapter<BottomGalleryListAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.clothes_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(
        private val itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        private val clothesImageView = itemView.findViewById<ImageView>(R.id.clothes_image)
        private val favoriteButton = itemView.findViewById<Button>(R.id.clothes_item_favorite_button)

        fun bind(detectedClothes: DetectedClothes) {
            clothesImageView.setImageBitmap(detectedClothes.sourceImage)

            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(detectedClothes.url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                it.context.startActivity(
                    intent,
                )
            }

        }


    }


}