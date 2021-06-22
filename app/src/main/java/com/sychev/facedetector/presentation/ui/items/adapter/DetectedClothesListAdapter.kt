package com.sychev.facedetector.presentation.ui.items.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorEvent
import com.sychev.facedetector.presentation.ui.detectorAssitant.DetectorViewModel
import com.sychev.facedetector.utils.TAG

class DetectedClothesListAdapter(val list: ArrayList<DetectedClothes>, private val viewModel: DetectorViewModel): RecyclerView.Adapter<DetectedClothesListAdapter.ClothesItem>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesItem {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ClothesItem(
            layoutInflater.inflate(R.layout.clothes_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ClothesItem, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ClothesItem(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val clothesImageView = itemView.findViewById<ImageView>(R.id.clothes_image)
        val closeButton = itemView.findViewById<Button>(R.id.clothes_item_close_button)
        val favoriteButton = itemView.findViewById<Button>(R.id.clothes_item_favorite_button)

        fun bind(detectedClothes: DetectedClothes, position: Int) {
            clothesImageView.setImageBitmap(detectedClothes.sourceImage)
            favoriteButton.background = if (detectedClothes.isFavorite) {
                ContextCompat.getDrawable(itemView.context, R.drawable.filled_heart)
            } else {
                ContextCompat.getDrawable(itemView.context, R.drawable.empty_heart)
            }

            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(detectedClothes.url))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                it.context.startActivity(
                    intent,
                )
            }
            favoriteButton.setOnClickListener {
                detectedClothes.isFavorite = !detectedClothes.isFavorite
                when (detectedClothes.isFavorite) {
                    true -> {
                        Log.d(
                            TAG,
                            "bind: favoriteButton clicked, isFavorite = ${detectedClothes.isFavorite}"
                        )
                        it.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.filled_heart)
                        viewModel.onTriggerEvent(
                            DetectorEvent.InsertClothesToFavoriteEvent(
                                detectedClothes
                            )
                        )
                    }
                    false -> {
                        Log.d(
                            TAG,
                            "bind: favoriteButton clicked, isFavorite = ${detectedClothes.isFavorite}"
                        )
                        it.background =
                            ContextCompat.getDrawable(itemView.context, R.drawable.empty_heart)
                        viewModel.onTriggerEvent(
                            DetectorEvent.DeleteDetectedClothesEvent(
                                detectedClothes
                            )
                        )
                    }
                }
//                if (detectedClothes.isFavorite) {
//                    it.background = ContextCompat.getDrawable(itemView.context, R.drawable.filled_heart)
//                    viewModel.onTriggerEvent(DetectorEvent.InsertClothesToFavoriteEvent(detectedClothes))
//                } else {
//                    it.background = ContextCompat.getDrawable(itemView.context, R.drawable.empty_heart)
//                    viewModel.onTriggerEvent(DetectorEvent.InsertClothesToFavoriteEvent(detectedClothes))
//                }
            }

            closeButton.setOnClickListener {
                list.removeAt(position)
                this@DetectedClothesListAdapter.notifyItemRemoved(position)
                this@DetectedClothesListAdapter.notifyItemRangeChanged(position, list.size)
            }

        }

    }

}

