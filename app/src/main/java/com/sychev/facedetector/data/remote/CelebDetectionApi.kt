package com.sychev.facedetector.data.remote

import com.sychev.facedetector.domain.Person
import okhttp3.Callback
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface CelebDetectionApi {

    @POST("get_name")
    suspend fun findCelebrity(
        @Body body: HashMap<Int, String>
    ): HashMap<Int, String>

}