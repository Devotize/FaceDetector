package com.sychev.facedetector.data.remote

import com.sychev.facedetector.data.remote.model.DetectedClothesDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.File


//https://searcher-ml02.rgsbank.ru:8083/docs
//https://rgsb-searcher-segment-ml02.rgsbank.ru:8083/docs

interface ClothesDetectionApi {


    @POST("search")
    suspend fun detectClothes(
    @Body requestBody: RequestBody,
    ): DetectedClothesDto

}