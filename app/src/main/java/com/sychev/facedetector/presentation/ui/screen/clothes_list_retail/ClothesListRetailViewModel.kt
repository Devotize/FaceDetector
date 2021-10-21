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
import com.sychev.facedetector.interactors.clothes.GetClothesList
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.interactors.clothes_list.ProcessClothesForRetail
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.activity.main.MainActivity
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.ClothesDetailEvent
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getClothesList: GetClothesList,
    private val searchClothes: SearchClothes,
    private val navigationManager: NavigationManager,
): ViewModel() {

    val clothesList = mutableStateListOf<Clothes>()
    val clothesChips = mutableStateListOf<Pair<Clothes, List<Clothes>>>()
    val selectedChip = mutableStateOf<Pair<Clothes, List<Clothes>>?>(null)
    val similarClothes = mutableStateMapOf<Int, List<Clothes>>()

    fun onTriggerEvent(event: ClothesListRetailEvent) {
        when (event) {
            is ClothesListRetailEvent.ProcessClothesEvent -> {
                processClothesForRetail.execute(event.clothes).onEach { dataState ->
                    dataState.data?.let {
                        it.forEachIndexed { index: Int, clothes: Clothes ->
                             if (clothes.provider == "wildberries") {
                                clothesChips.add(Pair(clothes, listOf(clothes, it[index+1])))
                            }
                        }
                        if (clothesChips.isNotEmpty()) {
                            onTriggerEvent(ClothesListRetailEvent.OnSelectChipEvent(clothesChips.first(), event.context))
                        }
                    }
                }.launchIn(viewModelScope)
//                CoroutineScope(IO).launch {
//                    updateChips()
//                }
            }
            is ClothesListRetailEvent.OnSelectChipEvent -> {
                onSelectedChipChanged(event.chip, event.context)
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

    private fun onSelectedChipChanged(newSelectedChip: Pair<Clothes, List<Clothes>>, context: Context) {
        selectedChip.value = newSelectedChip
        clothesList.clear()
        clothesList.addAll(newSelectedChip.second)
        similarClothes.clear()
        clothesList.forEachIndexed{index, clothes ->
            searchSimilarClothes(index = index, clothes = clothes, context = context)
        }
        refreshClothesList()
    }

    private fun addToFavorite(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes)
            .onEach {
                it.data?.let {
                   refreshClothesList()
                }

            }.launchIn(viewModelScope)
    }

    private fun removeFromFavorite(clothes: Clothes) {
        removeFromFavoriteClothes.execute(clothes)
            .onEach {
                 refreshClothesList()
            }.launchIn(viewModelScope)
    }

    private fun refreshClothesList() {
        getClothesList.execute(clothesList).onEach {dataState ->
            dataState.data?.let{
                clothesList.clear()
                clothesList.addAll(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun searchSimilarClothes(clothes: Clothes, context: Context, index: Int) {
        Log.d(TAG, "searchSimilarClothes: called index: $index")
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
                            Log.d(TAG, "onResourceReady: dataIsNotNull : $index: $index")
                            val list = it.toMutableList()
                            try {
                                list.remove(clothes)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            similarClothes[index] = list.toList()
                            Log.d(TAG, "onResourceReady: similarClothes: ${similarClothes.keys.toList()}")
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