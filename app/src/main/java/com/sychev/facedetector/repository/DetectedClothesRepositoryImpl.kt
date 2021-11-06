package com.sychev.facedetector.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.sychev.facedetector.data.local.dao.ClothesDao
import com.sychev.facedetector.data.local.dao.DetectedClothesDao
import com.sychev.facedetector.data.local.mapper.ClothesEntityConverter
import com.sychev.facedetector.data.local.mapper.DetectedClothesEntityConverter
import com.sychev.facedetector.data.remote.ClothesDetectionApi
import com.sychev.facedetector.data.remote.UnsplashApi
import com.sychev.facedetector.data.remote.converter.BrandDtoConverter
import com.sychev.facedetector.data.remote.converter.CelebDtoConverter
import com.sychev.facedetector.data.remote.converter.ClothesDtoConverter
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItem
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.ClothesWithBubbles
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.domain.celeb.Celeb
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.presentation.ui.screen.shop.ClothesFilter
import com.sychev.facedetector.utils.TAG
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DetectedClothesRepositoryImpl(
    private val clothesDetectionApi: ClothesDetectionApi,
    private val clothesDao: ClothesDao,
    private val detectedClothesDao: DetectedClothesDao,
    private val unsplashApi: UnsplashApi,
    private val clothesEntityConverter: ClothesEntityConverter,
    private val clothesDtoConverter: ClothesDtoConverter,
    private val brandDtoConverter: BrandDtoConverter,
    private val detectedClothesEntityConverter: DetectedClothesEntityConverter,
    private val celebDtoConverter: CelebDtoConverter,
): DetectedClothesRepository {

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    override suspend fun searchClothes(detectedClothes: DetectedClothes, context: Context, size: Int): List<Clothes> {
        val stream = ByteArrayOutputStream()
        detectedClothes.croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)
        val byteArrayBitmap = stream.toByteArray()
//        val bodyString = Base64.encodeToString(byteArrayBitmap, Base64.DEFAULT)

        val uuid = UUID.randomUUID()
        val file = File(context.cacheDir,"files\\image_${uuid}.jpg")
        file.createNewFile()
        val fos = FileOutputStream(file)
        fos.write(byteArrayBitmap)
        fos.flush()
        fos.close()

        //for testing porpuses
        val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val testfile = File(path, "test_image.jpg").writeBitmap(detectedClothes.croppedBitmap, Bitmap.CompressFormat.PNG, 85)
//        testfile.createNewFile()
//        val testFos = FileOutputStream(file)
//        testFos.write(byteArrayBitmap)
//        testFos.flush()
//        testFos.close()


        val requestBody: RequestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("img", file.name ,file.asRequestBody("image/jpg".toMediaTypeOrNull()))
                .build()

        Log.d(TAG, "searchClothes: imgType: ${detectedClothes.title}, gender: ${detectedClothes.gender}, size: $size")

        val result = clothesDetectionApi.searchClothesByCrop(
            imgType = detectedClothes.title,
            gender = detectedClothes.gender,
            size = size,
            requestBody = requestBody,
        )
//        Log.d(TAG, "searchClothes: result: $result")

        val wildberriesClothes = result.globalSearchResult.wildberries.searchResult
        val lamodaClothes = result.globalSearchResult.lamoda.searchResult
        val allClothes = wildberriesClothes.plus(lamodaClothes)

        if (file.exists()) {
//            file.delete()
        }
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

    override suspend fun getCelebPics(page: Int ): List<Celeb> {
        val result = clothesDetectionApi.getCelebPics(page = page)

        return result.map{celebDtoConverter.toDomainModel(it)}
    }

    override suspend fun searchClothesByQuery(query: String, size: Int): List<Clothes> {
        val hm = HashMap<String, Any>()
        hm.put("query", query)
        hm.put("size", size)
        val result = clothesDetectionApi.searchClothesByText(body = hm)

        return clothesDtoConverter.toDomainClothesList(result.searchResult)
    }

    override suspend fun searchClothesByFilters(filters: ClothesFilter): ClothesWithBubbles {
        val hm = HashMap<String, Any>()
        if (filters.genders.isNotEmpty()) {
            filters.genders.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item)
                }
                hm.put(ClothesFilter.Titles.gender, valueList)
            }
        }
        hm.put("search_size", filters.searchSize)
        if (filters.itemCategories.isNotEmpty()) {
            filters.itemCategories.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item)
                }
                hm.put(ClothesFilter.Titles.itemCategories, valueList)
            }
        }
        if (filters.itemCategories.isNotEmpty()) {
            filters.itemSubcategories.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item)
                }
                hm.put(ClothesFilter.Titles.itemSubcategories, valueList)
            }
        }
        if (filters.brands.isNotEmpty()) {
            filters.brands.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item)
                }
                hm.put(ClothesFilter.Titles.brands, valueList)
            }
        }

            filters.price.let {
                if (it.max != null) {
                    val prices = arrayOf(it.min, it.max)
                    hm.put(ClothesFilter.Titles.prices, prices)
                }
            }

        if (filters.itemSizes.isNotEmpty()) {
            filters.itemSizes.let {
                val valueList = ArrayList<Int>()
                it.forEach { item ->
                    valueList.add(item.toInt())
                }
                hm.put(ClothesFilter.Titles.itemSizes, valueList)
            }
        }
        if (filters.colors.isNotEmpty()) {
            filters.colors.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item)
                }
                hm.put(ClothesFilter.Titles.colors, valueList)
            }
        }
        if (filters.novice == FilterValues.Constants.Novice.new) {
            filters.novice.let {
                val valueList = ArrayList<Int>()
                valueList.add(it)
                hm.put(ClothesFilter.Titles.novice, true)
            }
        }
        if (filters.popular == FilterValues.Constants.Popular.popular) {
            filters.popular.let {
                val valueList = ArrayList<Int>()
                valueList.add(it)
                hm.put(ClothesFilter.Titles.popularFlags, true)
            }
        }
        if (filters.providers.isNotEmpty()) {
            filters.providers.let {
                val valueList = ArrayList<String>()
                it.forEach { item ->
                    valueList.add(item)
                }
                hm.put(ClothesFilter.Titles.providers, valueList)
            }
        }
        if (filters.fullTextQuery.isNotEmpty()) {
            filters.fullTextQuery.let {
                hm.put(ClothesFilter.Titles.fullTextQuery, it)
            }
        }
        Log.d(TAG, "searchClothesByFilters: hm: $hm")
        val result = clothesDetectionApi.searchClothesByFilters(hm)
        val clothesWithBubbles = ClothesWithBubbles(
            clothes = clothesDtoConverter.toDomainClothesList(result.searchResult),
            bubbles = result.bubbles
        )
        return clothesWithBubbles
    }

    override suspend fun getFilterValues(): List<FilterValuesDtoItem> {
        val result = clothesDetectionApi.getFilterValues()
        Log.d(TAG, "getFilterValues: $result")
        return result
    }

    override suspend fun getTopBrands(): List<Brand> {
        val result = clothesDetectionApi.getTopBrands()
        return brandDtoConverter.toDomainModelList(result)
    }

    override suspend fun getDetectedClothes(): List<DetectedClothes> {
        val result = detectedClothesDao.getDetectedClothes()
        return result.map {
            detectedClothesEntityConverter.toDomainModel(it)
        }
    }

    override suspend fun insertDetectedClothes( detectedClothes: List<DetectedClothes>): LongArray {
        val result = detectedClothesDao.insertDetectedClothes(detectedClothes.map { detectedClothesEntityConverter.fromDomainModel(it) })
        return result
    }

    override suspend fun clearDetectedClothesTable() {
        return detectedClothesDao.clearDetectedClothes()
    }
}












