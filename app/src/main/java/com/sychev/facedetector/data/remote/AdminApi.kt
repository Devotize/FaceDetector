package com.sychev.facedetector.data.remote

import com.sychev.facedetector.data.remote.model.FilterValuesDtoItem
import retrofit2.http.GET

interface AdminApi {
    @GET("front_configurator/front/read_filters")
    suspend fun readFilters(): List<FilterValuesDtoItem>
}