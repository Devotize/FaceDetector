package com.sychev.facedetector.presentation.ui.screen.clothes_list_retail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.interactors.clothes.GetClothes
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.interactors.clothes_list.ProcessClothesForRetail
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ClothesListRetailViewModel
@Inject
constructor(
    private val insertClothesToFavorite: InsertClothesToFavorite,
    private val removeFromFavoriteClothes: RemoveFromFavoriteClothes,
    private val processClothesForRetail: ProcessClothesForRetail,
    private val getClothes: GetClothes,
    private val searchClothes: SearchClothes,
    private val navigationManager: NavigationManager,
): ViewModel() {
    val clothesChips = mutableStateListOf<Clothes>()
    val clothesChipsContainsIndexes = mutableStateListOf<List<Int>>()
    val listOfClothesList = mutableStateListOf<ArrayList<Clothes>>()
    val similarClothes = mutableStateMapOf<Int, List<Clothes>>()
    val loading = mutableStateOf(false)
    @Inject lateinit var filterValues: FilterValues

    fun onTriggerEvent(event: ClothesListRetailEvent) {
        when (event) {
            is ClothesListRetailEvent.FindClothes -> {
                findClothes(event.detectedClothes, event.context)
            }
            is ClothesListRetailEvent.AddToFavoriteClothesEvent -> {
                addToFavorite(event.clothes)
            }
            is ClothesListRetailEvent.RemoveFromFavoriteClothesEvent -> {
                removeFromFavorite(event.clothes)
            }
            is ClothesListRetailEvent.GoToDetailScreen -> {
                val detailScreen = Screen.ClothesDetail.apply {
                    arguments = arrayListOf(event.clothes)
                }
                navigationManager.navigate(detailScreen)
            }
            is ClothesListRetailEvent.GetSimilarClothes -> {
                searchSimilarClothes(clothes = event.clothes, context = event.context, index = event.index)
            }
            is ClothesListRetailEvent.ShareClothesEvent -> {
                shareClothes(event.clothes, event.context)
            }
        }
    }

    var containIndexToAdd = 0
    private fun findClothes(detectedClothes: DetectedClothes, context: Context) {
        searchClothes.execute(
            detectedClothes = detectedClothes,
            context = context
        ).onEach { dataState ->
            loading.value = dataState.loading
            dataState.data?.let {
                listOfClothesList.add(it as ArrayList<Clothes>)
                clothesChips.add(it[0])
                val indexesToAdd = ArrayList<Int>()
                for (i in 0 until it.size) {
                    indexesToAdd.add(containIndexToAdd)
                    containIndexToAdd++
                }
                clothesChipsContainsIndexes.add(indexesToAdd)
            }
            dataState.error?.let {
                Log.d(TAG, "findClothes: error: $it")
            }
        }.launchIn(viewModelScope)
    }

    private fun addToFavorite(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes)
            .onEach {
                it.data?.let {
                   updateClothes(clothes)
                }

            }.launchIn(viewModelScope)
    }

    private fun removeFromFavorite(clothes: Clothes) {
        removeFromFavoriteClothes.execute(clothes)
            .onEach {
                 updateClothes(clothes = clothes)
            }.launchIn(viewModelScope)
    }

//    private fun refreshClothesList() {
//        getClothes.execute(clothesList).onEach { dataState ->
//            dataState.data?.let{
//                clothesList.clear()
//                clothesList.addAll(it)
//            }
//        }.launchIn(viewModelScope)
//    }

    private fun updateClothes(clothes: Clothes) {
        getClothes.execute(clothes = clothes).onEach { dataState ->
            dataState.data?.let { clothesFromCache ->
                listOfClothesList.forEach { cl ->
                    cl.forEachIndexed { index, item ->
                        if (item.clothesUrl == clothesFromCache.clothesUrl) {
                            cl[index] = clothesFromCache
                            cl[index] = cl[index].copy(isFavorite = clothesFromCache.isFavorite)

                        }
                    }
                }
                listOfClothesList.add(listOfClothesList[0])
                listOfClothesList.removeLast()
            }
        }.launchIn(viewModelScope)
    }

    private fun searchSimilarClothes(clothes: Clothes, context: Context, index: Int) {
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
                        dataState.data?.let {
                            val list = it.toMutableList()
                            try {
                                list.remove(clothes)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            similarClothes[index] = list.toList()
                        }
                    }.launchIn(viewModelScope)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

    }

    private fun shareClothes(clothes: Clothes, context: Context) {
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

}