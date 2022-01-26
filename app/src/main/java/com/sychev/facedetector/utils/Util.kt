package com.sychev.facedetector.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 90, stream)
    return stream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

@Composable
fun loadPicture(url: String?, @DrawableRes defaultImage: Int): MutableState<Bitmap?> {
    val bitmapState: MutableState<Bitmap?> = remember{mutableStateOf(null)}
    
    Glide.with(LocalContext.current)
        .asBitmap()
        .load(defaultImage)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })

    Glide.with(LocalContext.current)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })
    return bitmapState
}

@Composable
fun loadPicture(url: String): MutableState<Bitmap?> {
    val bitmapState: MutableState<Bitmap?> = remember{mutableStateOf(null)}

    Glide.with(LocalContext.current)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                Log.d(TAG, "onResourceReady: url: $url")
//                Log.d(TAG, "FeedListScreen: newBitmap: $resource")
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })
    return bitmapState
}

fun String.toMoneyString(): String {
    if (this.length > 3) {
        var reversed = this.reversed()
        val numOfSpaces = (reversed.length / 3).toInt()
        val finalString = StringBuilder()
        for (i in 0 until numOfSpaces) {
            finalString.append(reversed.substring(0..2))
            finalString.append(" ")
            reversed = reversed.removeRange(0..2)
        }
        finalString.append(reversed)
        return finalString.toString().reversed()
    }else {
        return this
    }
}

fun String.toWordsList(): List<String> {
    val words: ArrayList<String> = ArrayList()
    val originalString = StringBuilder()
    originalString.append(this)
    val stringBuilder = StringBuilder()
    while (originalString.isNotEmpty()) {
        if (originalString[0] != ' ') {
            stringBuilder.append(originalString[0])
        } else {
            words.add(stringBuilder.toString())
            stringBuilder.clear()
        }
        if (originalString.length == 1) {
            words.add(stringBuilder.toString())
        }
        originalString.deleteAt(0)
    }

    return words
}

fun Uri.toBitmap(context: Context): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images
            .Media.getBitmap(context.contentResolver, this)
    } else {
        val source = ImageDecoder
            .createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)
    }
}

fun IntRange.random() =
    Random().nextInt((endInclusive + 1) - start) + start

val String.color
    get() = Color(android.graphics.Color.parseColor(this))