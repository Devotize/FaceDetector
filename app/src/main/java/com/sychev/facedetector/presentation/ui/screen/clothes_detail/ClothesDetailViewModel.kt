package com.sychev.facedetector.presentation.ui.screen.clothes_detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.interactors.clothes.GetClothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.shop_screen.TestClothesFilter
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class ClothesDetailViewModel
@Inject constructor(
    private val searchClothes: SearchClothes,
    private val navigationManager: NavigationManager,
    private val insertClothesToFavorite: InsertClothesToFavorite,
    private val removeFromFavoriteClothes: RemoveFromFavoriteClothes,
    private val getClothes: GetClothes,
): ViewModel(){
    val similarClothes = mutableStateListOf<Clothes>()
    val loadingSimilarClothes = mutableStateOf(false)
    val clothes = mutableStateOf<Clothes?>(null)

    fun onTriggerEvent(event: ClothesDetailEvent) {
        when (event){
            is ClothesDetailEvent.SearchSimilarClothesEvent -> {
                searchSimilarClothes(event.clothes, event.context)
            }
            is ClothesDetailEvent.GoToDetailScreen -> {
                val screen = Screen.ClothesDetail.apply {
                    arguments = arrayListOf(event.clothes)
                }
                navigationManager.navigate(screen)
            }
            is ClothesDetailEvent.GetClothesFromCache -> {
                getClothesFromCache(event.clothes)
            }
            is ClothesDetailEvent.AddToFavoriteClothesEvent -> {
                addToFavoriteClothes(event.clothes)
            }
            is ClothesDetailEvent.RemoveFromFavoriteClothesEvent -> {
                removeFromFavoriteClothes(event.clothes)
            }
            is ClothesDetailEvent.ShareClothesEvent -> {
                shareClothes(event.context, event.clothes)
            }
        }
    }

    private fun shareClothes(context: Context, clothes: Clothes) {
        Glide.with(context)
            .asBitmap()
            .load(clothes.picUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val bytes = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path: String = MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        resource,
                        clothes.brand.plus(" ${clothes.itemCategory}"),
                        null
                    )
                    val imageUri = Uri.parse(path)
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.putExtra(Intent.EXTRA_TEXT, clothes.clothesUrl)
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                    shareIntent.type = "image/jpeg"
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(Intent.createChooser(shareIntent, "send"))
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    private fun removeFromFavoriteClothes(clothes: Clothes) {
        Log.d(TAG, "removeFromFavoriteClothes: called")
        removeFromFavoriteClothes.execute(clothes)
            .onEach { dataState ->
                dataState.data?.let {
                    getClothesFromCache(clothes)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addToFavoriteClothes(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes)
            .onEach { dataState ->
                dataState.data?.let {
                    getClothesFromCache(clothes)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getClothesFromCache(clothes: Clothes) {
        getClothes.execute(clothes)
            .onEach {dataState ->
                dataState.data?.let {
                    Log.d(TAG, "getClothesFromCache: data: $clothes")
                    this.clothes.value = null
                    this.clothes.value = it
                }
            }.launchIn(viewModelScope)
    }

    private fun searchClothesByQuery(query: String, size: Int) {
        val filters = TestClothesFilter()
        filters.fullTextQuery = query
        filters.searchSize = size
        searchClothes.execute(filters)
            .onEach { dataState ->
            loadingSimilarClothes.value = dataState.loading
                dataState.data?.let {
                    Log.d(TAG, "searchClothesByQuery: similarClothes: $it")
                    similarClothes.addAll(it.clothes)
                }

            }.launchIn(viewModelScope)
    }

    private fun searchSimilarClothes(clothes: Clothes, context: Context) {
        Glide.with(context)
            .asBitmap()
            .load(clothes.picUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val detectedClothes = DetectedClothes(
                        sourceBitmap = resource,
                        croppedBitmap = resource,
                        gender = clothes.gender,
                        title = clothes.itemCategory,
                        location = RectF()
                    )
                    searchClothes.execute(detectedClothes, context, size = 6).onEach { dataState ->
                        similarClothes.clear()
                        dataState.data?.let {
                            val list = it.toMutableList()
                            try {
                                list.remove(clothes)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            similarClothes.addAll(list.toList())
                        }
                    }.launchIn(viewModelScope)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

    }

}






