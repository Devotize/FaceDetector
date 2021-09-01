package com.sychev.facedetector.presentation.ui.screen.shop_screen

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.interactors.clothes.GetClothesList
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ShopViewModel
@Inject constructor(
    private val searchClothes: SearchClothes,
    private val removeFromFavoriteClothes: RemoveFromFavoriteClothes,
    private val insertClothesToFavorite: InsertClothesToFavorite,
    private val getClothesList: GetClothesList,
    private val navigationManager: NavigationManager
) : ViewModel() {
    private val defaultSearchSize = 40
    val loading = mutableStateOf(false)
    val query = mutableStateOf("")
    val gender = mutableStateOf<ClothesFilters.Gender?>(null)
    val clothesList = mutableStateListOf<Clothes>()
    val filters = mutableStateListOf<ClothesFilters>().apply {
        addAll(ClothesFilters.defaultFilters())
    }
    private val selectedFilter = mutableStateOf<ClothesFilters?>(null)
    val customFilter = mutableStateOf<ClothesFilters>(ClothesFilters())

    init {
        findClothesForFilters()
    }

    fun onTriggerEvent(event: ShopEvent) {
        when (event) {
            is ShopEvent.SearchByFilters -> {
                performSearchByFilters(
                    event.filters
                )
            }
            is ShopEvent.OnQueryChange -> {
                changeQuery(event.query)
            }
            is ShopEvent.PerformSearchByQuery -> {
                performSearch(query = query.value, size = defaultSearchSize)
            }
            is ShopEvent.OnGenderChange -> {
                onGenderChange(gender = event.gender)
            }
            is ShopEvent.AddToFavoriteClothesEvent -> {
                addToFavoriteClothes(event.clothes)
            }
            is ShopEvent.RemoveFromFavoriteClothesEvent -> {
                removeFromFavoriteClothes(event.clothes)
            }
            is ShopEvent.GoToFiltersScreen -> {
                navigationManager.navigate(Screen.FiltersScreen)
            }
            is ShopEvent.GotBackToShopScreen -> {
                navigationManager.navigate(Screen.Shop) {
                    popUpTo(Screen.Shop.route)
                }
            }
            is ShopEvent.ChangeCustomFilters -> {
                onCustomFilterChange(event.newFilters)
            }
            is ShopEvent.SaveCustomClothesFilter -> {
                onSaveCustomFilters()
            }
        }
    }

    private fun removeFromFavoriteClothes(clothes: Clothes) {
        removeFromFavoriteClothes.execute(clothes)
            .onEach { dataState ->
                dataState.data?.let {
                    refreshClothesList()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun addToFavoriteClothes(clothes: Clothes) {
        insertClothesToFavorite.execute(clothes)
            .onEach {
                it.data?.let {
                    refreshClothesList()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onGenderChange(gender: ClothesFilters.Gender?) {
        this.gender.value = gender
        filters.forEach {
            if (gender == null) {
                it.gender = ArrayList()
            } else {
                it.gender = arrayListOf(gender)
            }
        }
        if (clothesList.isEmpty()) {
            findClothesForFilters()
        } else {
            Log.d(TAG, "onGenderChange: selectedFilter value: ${selectedFilter.value}")
            if (selectedFilter.value == null) {
                performSearch(query = query.value, size = defaultSearchSize)
            } else {
                selectedFilter.value?.let { performSearchByFilters(it) }
            }
        }
    }

    private fun performSearchByFilters(
        filters: ClothesFilters
    ) {
        searchClothes.execute(
            filters.apply {
                size = this@ShopViewModel.defaultSearchSize
            }
        ).onEach { dataState ->
            loading.value = dataState.loading
            dataState.data?.let {
                clothesList.clear()
                clothesList.addAll(it)
                selectedFilter.value = filters
            }
        }.launchIn(viewModelScope)
    }

    private fun changeQuery(newQuery: String) {
        query.value = newQuery
    }

    private fun performSearch(query: String, size: Int) {
        if (query.isEmpty()) return
        val queryWithGender = if (gender.value != null) query.plus(" ${gender.value?.title}") else query
        searchClothes.execute(queryWithGender, size)
            .onEach { dataState ->
                loading.value = dataState.loading
                dataState.data?.let {
                    clothesList.clear()
                    clothesList.addAll(it)
                    selectedFilter.value = null
                }
            }.launchIn(viewModelScope)
    }

    private fun refreshClothesList() {
        getClothesList.execute(clothesList).onEach { dataState ->
            dataState.data?.let {
                clothesList.clear()
                clothesList.addAll(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun findClothesForFilters() {
        var multiIndex = 0
        filters.forEachIndexed { index, filter ->
            if (index == multiIndex) {
                filter.size = 4
                multiIndex += 3
            }
            searchClothes.execute(filter)
                .onEach { dataState: DataState<List<Clothes>> ->
                    loading.value = dataState.loading
                        dataState.data?.let {
                        filter.clothes = it
                        val clothesFilters = ArrayList<ClothesFilters>()
                        clothesFilters.addAll(filters)
                        filters.clear()
                        filters.addAll(clothesFilters)
                    }
                }.launchIn(viewModelScope)
        }
    }

    private fun onCustomFilterChange(newFilters: ClothesFilters) {
        customFilter.value = ClothesFilters()
        customFilter.value = newFilters
    }

    private fun onSaveCustomFilters() {
        filters.add(customFilter.value)
        customFilter.value = ClothesFilters()
        onTriggerEvent(ShopEvent.GotBackToShopScreen)
        findClothesForFilters()
    }

}










