package com.sychev.facedetector.presentation.ui.items.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes

class DetectedClothesListAdapter(val list: ArrayList<DetectedClothes>): RecyclerView.Adapter<DetectedClothesListAdapter.ClothesItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesItem {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ClothesItem(
            layoutInflater.inflate(R.layout.clothes_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ClothesItem, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ClothesItem(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val clothesImageView = itemView.findViewById<ImageView>(R.id.clothes_image)

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

