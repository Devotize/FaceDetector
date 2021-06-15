package com.sychev.facedetector.data.remote

import com.sychev.facedetector.data.remote.model.DetectedClothesDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.File

interface ClothesDetectionApi {

//    @FormUrlEncoded
//    @Multipart
    @POST("search")
    suspend fun detectClothes(
//    @Part file: MultipartBody.Part,
    @Body requestBody: RequestBody,
//    @Field("img", encoded = true) file: RequestBody
    ): DetectedClothesDto

}