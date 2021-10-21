package com.sychev.facedetector.interactors.filter

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItemOld
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.domain.filter.ColorsFilterValue
import com.sychev.facedetector.domain.filter.FilterValue
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.domain.filter.Price
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFilterValues(
    private val clothesRepository: DetectedClothesRepository,
    private val filterValues: FilterValues,
) {
    fun execute(): Flow<DataState<FilterValuesDtoItemOld>> = flow<DataState<FilterValuesDtoItemOld>> {
        try {

        emit(DataState.loading())
        val result = clothesRepository.getFilterValues()
        filterValues.genders = listOf("женский", "мужской")
        filterValues.providers = listOf("wildberries", "lamoda")
        result.forEach { filterValuesDtoItem ->
            val filterValue = FilterValue(
                id = filterValuesDtoItem.id,
                name = filterValuesDtoItem.name
            )
            when (filterValuesDtoItem.id) {
                "category" -> {
                    filterValuesDtoItem.values?.let { listOfAny ->
                        val list: List<String> = listOfAny.map {
                            it.toString()
                        }
                        filterValues.itemCategories = Pair(filterValue, list)
                    }
                }
                "subcategory" -> {
                    filterValuesDtoItem.values?.let { listOfAny ->
                        val list: List<String> = listOfAny.map {
                            it.toString()
                        }
                        filterValues.itemSubcategories = Pair(filterValue, list)
                    }
                }
                "brand" -> {
                    filterValuesDtoItem.values?.let { listOfAny ->
                        val list: List<String> = listOfAny.map {
                            it.toString()
                        }
                        filterValues.brands = Pair(filterValue, list)
                    }
                }
                "size" -> {
                    filterValuesDtoItem.values?.let {
                        val list = it.map { (it as Double).toInt().toString() }
                        filterValues.itemSizes = list
                    }
                }
                "colour" -> {
                    filterValuesDtoItem.values?.let {
                        val maps = it as List<LinkedTreeMap<*,*>>
                        val list = ArrayList<ColorsFilterValue>()
                        for (m in maps) {
//                            Log.d(TAG, "execute: m.values: ${m.values.asIterable().iterator().next()}")
                            list.add(
                                ColorsFilterValue(
                                    colorName = m.values.asIterable().toList()[0].toString(),
                                    colorHex = m.values.asIterable().toList()[1].toString(),
                            ))
                        }
                        filterValues.colors = Pair(filterValue, list)
                    }
                }
                "price" -> {
                    filterValuesDtoItem.range?.let { list ->
                        Log.d(TAG, "execute: prices list: $list")
                        filterValues.price = Price(
                            min = list[0],
                            max = list[1]
                        )
                        Log.d(TAG, "execute: prices: ${filterValues.price}")
                    }
                }
//                "provider" -> {
//                    filterValuesDtoItem.values?.let {
//                        val maps = it as List<LinkedTreeMap<*,*>>
//                        Log.d(TAG, "execute: colour $maps")
//                        val list = ArrayList<ProviderFilterValue>()
//                        for (m in maps) {
////                            Log.d(TAG, "execute: m.values: ${m.values.asIterable().iterator().next()}")
//                            list.add(
//                               ProviderFilterValue(
//                                    providerId = m.values.asIterable().toList()[0].toString(),
//                                    providerDisplayName = m.values.asIterable().toList()[1].toString(),
//                                    id = filterValuesDtoItem.id,
//                                    name = filterValuesDtoItem.name
//                                ))
//                        }
//                        filterValues.providers = list
//                    }
//                    }
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
    }
