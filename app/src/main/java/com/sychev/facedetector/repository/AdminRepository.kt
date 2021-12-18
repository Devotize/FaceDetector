package com.sychev.facedetector.repository

import com.sychev.facedetector.data.remote.model.FilterValuesDtoItem

interface AdminRepository {
    suspend fun getFilterValues(): List<FilterValuesDtoItem>
}