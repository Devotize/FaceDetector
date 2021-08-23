package com.sychev.facedetector.data.remote

import com.sychev.facedetector.data.remote.model.UnsplashDto
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.unsplash.com/photos/random/

interface UnsplashApi {
    @GET("photos/random/")
    suspend fun getPhotos(
        @Query("client_id") accessKey: String,
        @Query("query") query: String,
        @Query("count") count: Int
    ):UnsplashDto
}