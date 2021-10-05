package com.sychev.facedetector.interactors.filter

import android.util.Log
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItemOld
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFilterValues(
    private val clothesRepository: DetectedClothesRepository,
    private val filterValues: FilterValues,
) {
    fun execute(): Flow<DataState<FilterValuesDtoItemOld>> = flow<DataState<FilterValuesDtoItemOld>> {
        emit(DataState.loading())
        val result = clothesRepository.getFilterValues()
        filterValues.genders = listOf("женский", "мужской")
        filterValues.providers = listOf("wildberries", "lamoda")
        result.forEach { filterValuesDtoItem ->
            Log.d(TAG, "execute: filterValuesDtoItem: $filterValuesDtoItem")
            when (filterValuesDtoItem.id) {
//                "item_category" -> {
//                    filterValuesDtoItem.values?.let {
//                        filterValues.itemCategories = it
//                    }
//                }
//                "subcategory" -> {
//                    filterValuesDtoItem.values?.let {
//                        filterValues.itemSubcategories = it
//                    }
//                }
//                "brand" -> {
//                    filterValuesDtoItem.values?.let {
//                        filterValues.brands = it
//                    }
//                }
//                "size" -> {
//                    filterValuesDtoItem.values?.let {
//                        filterValues.itemSizes = it
//                        Log.d(TAG, "execute: filterValues.itemSizes: ${filterValues.itemSizes}")
//                    }
//                }
//                "colour" -> {
//                    filterValuesDtoItem.values?.let {
//                        filterValues.colors = it
//                    }
//                }
//                "provider" -> {
//                    filterValuesDtoItem.values?.let {
//                        filterValues.providers = it
//                    }
//                }
            }
        }
    }
}