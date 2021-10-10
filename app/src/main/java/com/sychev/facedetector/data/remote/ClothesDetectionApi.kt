package com.sychev.facedetector.data.remote

import com.sychev.facedetector.data.remote.model.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


//https://rgsb-back-ml02.mlcrm.rgsbank.ru:8083

interface ClothesDetectionApi {


    @POST("searchByCrop")
    suspend fun searchClothesByCrop(
        @Query("img_type") imgType: String,
        @Query("gender") gender: String,
        @Query("search_size") size: Int,
        @Body requestBody: RequestBody,
    ): SearchClothesResult

    @GET("endlessCelebrities")
    suspend fun getCelebPics(
        @Query("page_num") page: Int
    ): ResponseBody

    @POST("searchFullText")
    suspend fun searchClothesByText(
        @Body body: HashMap<String, Any>,
    ): SearchResult

    @POST("searchByFilters")
    suspend fun searchClothesByFilters(
        @Body body: HashMap<String, Any>
    ): SearchResult

    @GET("getFilterValues")
    suspend fun getFilterValues(): List<FilterValuesDtoItem>

    @GET("getTopBrands")
    suspend fun getTopBrands(): List<BrandDto>

}