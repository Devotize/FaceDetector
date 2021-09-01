package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.sychev.facedetector.data.local.dao.ClothesDao
import com.sychev.facedetector.data.local.mapper.ClothesEntityConverter
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.UnsplashApi
import com.sychev.facedetector.data.remote.converter.ClothesDtoConverter
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.ui.screen.shop_screen.ClothesFilters
import com.sychev.facedetector.utils.TAG
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class DetectedClothesRepositoryImpl(
    private val clothesDetectionApi: ClothesDetectionApi,
    private val clothesDao: ClothesDao,
    private val unsplashApi: UnsplashApi,
    private val clothesEntityConverter: ClothesEntityConverter,
    private val clothesDtoConverter: ClothesDtoConverter,
): DetectedClothesRepository {

    override suspend fun searchClothes(detectedClothes: DetectedClothes, context: Context): List<Clothes> {
        val stream = ByteArrayOutputStream()
        detectedClothes.croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
        val byteArrayBitmap = stream.toByteArray()
//        val bodyString = Base64.encodeToString(byteArrayBitmap, Base64.DEFAULT)

        val file = File(context.cacheDir,"image_test.jpg")
        file.createNewFile()
        val fos = FileOutputStream(file)
        fos.write(byteArrayBitmap)
        fos.flush()
        fos.close()

        val requestBody: RequestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("img", file.name ,file.asRequestBody("image/jpg".toMediaTypeOrNull()))
                .build()


        val result = clothesDetectionApi.searchClothesByCrop(
            imgType = detectedClothes.title,
            gender = detectedClothes.gender,
            size = 1,
            requestBody = requestBody,
        )

        val wildberriesClothes = result.searchResult.wildberries
        val lamodaClothes = result.searchResult.lamoda
        val allClothes = wildberriesClothes.plus(lamodaClothes)

        return clothesDtoConverter.toDomainClothesList(allClothes)
    }

    override suspend fun getClothesByUrl(url: String): Clothes {
        return clothesEntityConverter.toDomainModel(clothesDao.getClothesByUrl(url))
    }

    override suspend fun insertClothesToCache(clothesList: List<Clothes>) {
        Log.d(TAG, "insertDetectedClothesToCache: called detectedClothesList = $clothesList")
        clothesList.forEach {
            clothesDao.insertDetectedClothes(clothesEntityConverter.fromDomainModel(it))
        }
    }

    override suspend fun updateClothes(clothes: Clothes) {
        clothesDao.updateDetectedClothes(clothesEntityConverter.fromDomainModel(clothes))
    }

    override suspend fun getClothesList(clothesList: List<Clothes>): List<Clothes> {
        return clothesEntityConverter.toDomainModelList(clothesDao.getClothesListByUrl(clothesEntityConverter.fromDomainList(clothesList)))

    }

    override suspend fun getClothesList(): List<Clothes> {
        return clothesEntityConverter.toDomainModelList(clothesDao.getAllClothes())
    }

    override suspend fun getClothesList(numOfElements: Int): List<Clothes> {
        return clothesEntityConverter.toDomainModelList(clothesDao.getClothes(numOfElements))
    }

    override suspend fun insertClothesOrIgnoreIfFavorite(clothesList: List<Clothes>) {
        clothesDao.insertOrIgnoreIfInFavorite(clothesEntityConverter.fromDomainList(clothesList))
    }

    override suspend fun getFavoriteClothes(): List<Clothes> {
        return clothesEntityConverter.toDomainModelList(clothesDao.getAllFavoriteClothes())
    }

    override suspend fun deleteClothesFromCache(clothes: Clothes) {
        Log.d(TAG, "deleteDetectedClothesFromCache: called")
        clothesDao.deleteDetectedClothes(clothesEntityConverter.fromDomainModel(clothes))
    }

    override suspend fun getRandomPhotosUrl(
        accessKey: String,
        query: String,
        count: Int
    ): List<String> {
        val result = unsplashApi.getPhotos(
            accessKey, query, count
        )
        return result.map {
            it.urls.regular
        }
    }

    override suspend fun getCelebPics(page: Int ): List<Bitmap> {
        val response = clothesDetectionApi.getCelebPics(page = page)
        val responseString = response.string()
        val quotesIndexes = ArrayList<Int>()
        val endQuoteIndexes = ArrayList<Int>()
        val strings = ArrayList<String>()
        responseString.forEachIndexed{index: Int, c: Char ->
            if (c == '"') {
                quotesIndexes.add(index)
            }
        }
        quotesIndexes.forEachIndexed { index, quoteIndex ->
            if (index != quotesIndexes.lastIndex && !endQuoteIndexes.contains(quoteIndex)) {
                val str = responseString.substring(startIndex = quoteIndex + 1, endIndex = quotesIndexes[index+1])
                endQuoteIndexes.add(quotesIndexes[index + 1])
                strings.add(str)
            }
        }
//        Log.d(TAG, "getCelebPics: indexes: $quotesIndexes")
//        Log.d(TAG, "getCelebPics: strings: ${strings[2]}, ${strings.last()}")
        val bitmapList = ArrayList<Bitmap>()
        strings.forEachIndexed { index, s ->
            if (index%2 != 0) {
                val imageBytes = Base64.decode(s,0)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                bitmapList.add(bitmap)
            }
        }
        return bitmapList
    }

    override suspend fun searchClothesByQuery(query: String, size: Int): List<Clothes> {
        val hm = HashMap<String, Any>()
        hm.put("query", query)
        hm.put("size", size)
        val result = clothesDetectionApi.searchClothesByText(body = hm)
        return clothesDtoConverter.toDomainClothesList(result)
    }

    override suspend fun searchClothesByFilters(
        filters: ClothesFilters,
    ): List<Clothes> {
        val hm = HashMap<String, Any>()
        if (filters.gender.isNotEmpty()) {
            filters.gender.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item.title)
                }
                hm.put("genders", valueList)
            }
        }
        hm.put("size", filters.size)
        if (filters.itemCategories.isNotEmpty()) {
            filters.itemCategories.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item.title)
                }
                hm.put("item_categories", valueList)
            }
        }
        if (filters.itemCategories.isNotEmpty()) {
            filters.itemSubcategories.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item.title)
                }
                hm.put("item_subcategories", valueList)
            }
        }
        if (filters.brands.isNotEmpty()) {
            filters.brands?.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item.title)
                }
                hm.put("brands", valueList)
            }
        }
        if (filters.prices.isNotEmpty()) {
            filters.prices.let {
                hm.put("prices", it)
            }
        }
        if (filters.itemSizes.isNotEmpty()) {
            filters.itemSizes.let {
                val valueList = ArrayList<Int>()
                it.forEach { item ->
                    valueList.add(item.size)
                }
                hm.put("item_sizes", valueList)
            }
        }
        if (filters.colors.isNotEmpty()) {
            filters.colors.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item.title)
                }
                hm.put("colours", valueList)
            }
        }
        if (filters.novice.isNotEmpty()) {
            filters.novice.let {
                val valueList = ArrayList<Int>()
                it.forEach { item ->
                    valueList.add(item.index)
                }
                hm.put("novice_flgs", valueList)
            }
        }
        if (filters.popularFlags.isNotEmpty()) {
            filters.popularFlags.let {
                val valueList = ArrayList<Int>()
                it.forEach { item ->
                    valueList.add(item.index)
                }
                hm.put("popular_flgs", valueList)
            }
        }
        if (filters.providers.isNotEmpty()) {
            filters.providers.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item.title)
                }
                hm.put("providers", valueList)
            }
        }

        val result = clothesDetectionApi.searchClothesByFilters(hm)
        return clothesDtoConverter.toDomainClothesList(result)
    }
}












