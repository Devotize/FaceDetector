package com.sychev.facedetector.repository

import android.util.Log
import com.sychev.facedetector.data.remote.AdminApi
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItem
import com.sychev.facedetector.utils.TAG

class AdminRepositoryImpl(
    private val adminApi: AdminApi,
): AdminRepository {
    override suspend fun getFilterValues(): List<FilterValuesDtoItem> {
        Log.d(TAG, "getFilterValues: called")
        return adminApi.readFilters()
    }

}