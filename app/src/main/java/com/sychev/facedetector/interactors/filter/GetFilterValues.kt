package com.sychev.facedetector.interactors.filter

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItem
import com.sychev.facedetector.data.remote.model.FilterValuesDtoItemOld
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.domain.filter.*
import com.sychev.facedetector.repository.AdminRepository
import com.sychev.facedetector.repository.DetectedClothesRepository
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFilterValues(
    private val adminRepository: AdminRepository,
    private val filterValues: FilterValues,
) {
    fun execute(): Flow<DataState<FilterValuesDtoItem>> =
        flow<DataState<FilterValuesDtoItem>> {
            try {
                emit(DataState.loading())
                val result = adminRepository.getFilterValues()
                filterValues.genders = listOf("женский", "мужской")
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
                                Log.d(TAG, "getFilterValues: category: ${filterValues.itemCategories}")
                            }
                        }
                        "subcategory" -> {
                            filterValuesDtoItem.values?.let { listOfAny ->
                                val list: List<String> = listOfAny.map {
                                    it.toString()
                                }
                                filterValues.itemSubcategories = Pair(filterValue, list)
                                Log.d(TAG, "getFilterValues: subcategory: ${filterValues.itemSubcategories}")
                            }
                        }
                        "brand" -> {
                            filterValuesDtoItem.values?.let { listOfAny ->
                                val list: List<String> = listOfAny.map {
                                    it.toString()
                                }
                                filterValues.brands = Pair(filterValue, list)
                                Log.d(TAG, "getFilterValues: brand: ${filterValues.brands}")

                            }
                        }
                        "size" -> {
                            filterValuesDtoItem.values?.let {
                                val list = it as List<String>
                                filterValues.itemSizes = list
                                Log.d(TAG, "getFilterValues: size: ${filterValues.itemSizes}")
                            }
                        }
                        "colour" -> {
                            filterValuesDtoItem.values?.let {
                                val maps = it as List<LinkedTreeMap<*, *>>
                                val list = ArrayList<ColorsFilterValue>()
                                for (m in maps) {
//                            Log.d(TAG, "execute: m.values: ${m.values.asIterable().iterator().next()}")
                                    list.add(
                                        ColorsFilterValue(
                                            colorName = m.values.asIterable()
                                                .toList()[0].toString(),
                                            colorHex = m.values.asIterable().toList()[1].toString(),
                                        )
                                    )
                                }
                                filterValues.colors = Pair(filterValue, list)
                                Log.d(TAG, "getFilterValues: color: ${filterValues.colors}")

                            }
                        }
                        "price" -> {
                            filterValuesDtoItem.range?.let { list ->
                                Log.d(TAG, "execute: prices list: $list")
                                filterValues.price = Price(
                                    min = list[0],
                                    max = list[1]
                                )
                                Log.d(TAG, "getFilterValues: price: ${filterValues.price}")
                            }
                        }
                        "provider" -> {
                            filterValuesDtoItem.values?.let {
                                val providersMapList = it as List<LinkedTreeMap<String, String>>
                                val providers: MutableList<Provider> = mutableListOf()
                                providersMapList.forEach {
                                    providers.add(
                                        Provider(
                                            id = it.values.asIterable().toList()[0],
                                            displayName = it.values.asIterable().toList()[1],
                                        )
                                    )
                                }
                                filterValues.providers = providers.toList()
                                Log.d(TAG, "getFilterValues: providers: ${filterValues.providers}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
