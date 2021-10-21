package com.sychev.facedetector.presentation.ui.screen.shop_screen

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.brand.Brand
import com.sychev.facedetector.domain.data.DataState
import com.sychev.facedetector.domain.filter.FilterValues
import com.sychev.facedetector.interactors.brand.GetTopBrands
import com.sychev.facedetector.interactors.clothes.GetClothesList
import com.sychev.facedetector.interactors.clothes.InsertClothesToFavorite
import com.sychev.facedetector.interactors.clothes.RemoveFromFavoriteClothes
import com.sychev.facedetector.interactors.clothes_list.SearchClothes
import com.sychev.facedetector.presentation.ui.navigation.NavigationManager
import com.sychev.facedetector.presentation.ui.navigation.Screen
import com.sychev.facedetector.presentation.ui.screen.clothes_detail.ClothesDetailEvent
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
    private val navigationManager: NavigationManager,
    private val getTopBrands: GetTopBrands,
) : ViewModel() {
    private val defaultSearchSize = 40
    val loading = mutableStateOf(false)
    val query = mutableStateOf("")
    val gender = mutableStateOf<String?>(null)
    val clothesList = mutableStateListOf<Clothes>()
    val filters = mutableStateListOf<TestClothesFilter>().apply {
        addAll(TestClothesFilter.Filters.defaultFilters)
    }
    val selectedFilter = mutableStateOf<TestClothesFilter?>(null)
    val queryBubbles = mutableStateListOf<String>()
    val topBrands = mutableStateListOf<Brand>()
    val customFilter = mutableStateOf<TestClothesFilter>(TestClothesFilter())


    @Inject
    lateinit var filterValues: FilterValues


    init {
        findClothesForFilters()
        onTriggerEvent(ShopEvent.GetTopBrandsEvent)
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
                event.customFilter?.let {
                    customFilter.value = it
                }
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
            is ShopEvent.GoToDetailClothesScreen -> {
                val screen = Screen.ClothesDetail.apply {
                    arguments = arrayListOf(event.clothes)
                }
                navigationManager.navigate(screen)
            }
            is ShopEvent.ReplaceFilterByIndex -> {
                replaceFilterByIndex(event.index)
            }
            is ShopEvent.GetTopBrandsEvent -> {
                getTopBrands()
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

    private fun onGenderChange(gender: String?) {
        this.gender.value = gender
        filters.forEach {
            if (gender == null) {
//                it.gender = ArrayList()
                it.genders.clear()
            } else {
//                it.gender = arrayListOf(gender)
                it.genders.clear()
                it.genders.add(gender)
            }
        }
        if (clothesList.isEmpty()) {
            findClothesForFilters()
        } else {
            Log.d(TAG, "onGenderChange: selectedFilter value: ${selectedFilter.value}")
            if (selectedFilter.value == null) {
                performSearch(query = query.value, size = defaultSearchSize)
            } else {
                selectedFilter.value?.let {
                    it.genders.clear()
                    if (gender != null) {
                        it.genders.add(gender)
                    }
                    performSearchByFilters(it)
                }
            }
        }
    }

    private fun performSearchByFilters(
        filters: TestClothesFilter
    ) {
        searchClothes.execute(
            filters.apply {
                searchSize = this@ShopViewModel.defaultSearchSize
            }
        ).onEach { dataState ->
            loading.value = dataState.loading
            dataState.data?.let {
                Log.d(TAG, "performSearchByFilters: queryBubbles: ${it.bubbles}")
                if (it.clothes.isNotEmpty()) {
                    clothesList.clear()
                    clothesList.addAll(it.clothes)
                }
                var newQuery = ""
                it.bubbles.forEachIndexed() { index, str ->
                    newQuery += if (index != 0) {
                        " $str"
                    } else {
                        str
                    }
                }
                query.value = newQuery
                queryBubbles.clear()
                queryBubbles.addAll(it.bubbles)
                selectedFilter.value = null
                val newFilter = TestClothesFilter()
                newFilter.apply {
                    filters.let { filter ->
                        title = filter.title
                        genders.addAll(filter.genders)
                        itemCategories.addAll(filter.itemCategories)
                        itemSubcategories.addAll(filter.itemSubcategories)
                        brands.addAll(filter.brands)
                        itemSizes.addAll(filter.itemSizes)
                        colors.addAll(filter.colors)
                        providers.addAll(filter.providers)
                        price = filter.price
                        novice = filter.novice
                        popular = filter.popular
                        searchSize = filter.searchSize
                        fullTextQuery = filter.fullTextQuery
                        clothes = filter.clothes
                    }
                }
                selectedFilter.value = newFilter
            }
        }.launchIn(viewModelScope)
    }

    private fun changeQuery(newQuery: String) {
        query.value = newQuery
    }

    private fun performSearch(query: String, size: Int) {
        if (query.isEmpty()) return
        val filter = TestClothesFilter().apply {
            fullTextQuery = query
            searchSize = defaultSearchSize
            this@ShopViewModel.gender.value?.let {
                genders.clear()
                genders.add(it)
            }
        }
        searchClothes.execute(filter)
            .onEach { dataState ->
                loading.value = dataState.loading
                dataState.data?.let {
                    clothesList.clear()
                    clothesList.addAll(it.clothes)
                    queryBubbles.clear()
                    queryBubbles.addAll(it.bubbles)
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
                filter.searchSize = 4
                multiIndex += 3
            }
            searchClothes.execute(filter)
                .onEach { dataState ->
                    loading.value = dataState.loading
                    dataState.data?.let {
                        filter.clothes = it.clothes
                        val clothesFilters = ArrayList<TestClothesFilter>()
                        clothesFilters.addAll(filters)
                        filters.clear()
                        filters.addAll(clothesFilters)
                    }
                }.launchIn(viewModelScope)
        }
    }

    private fun onCustomFilterChange(newFilters: TestClothesFilter) {
        customFilter.value = TestClothesFilter()
        customFilter.value = newFilters
    }

    private fun onSaveCustomFilters() {
        filters.add(customFilter.value)
        customFilter.value = TestClothesFilter()
        onTriggerEvent(ShopEvent.GotBackToShopScreen)
        findClothesForFilters()
    }

    private fun replaceFilterByIndex(index: Int) {
        filters[index] = customFilter.value
        customFilter.value = TestClothesFilter()
        onTriggerEvent(ShopEvent.GotBackToShopScreen)
        findClothesForFilters()
    }

    private fun getTopBrands() {
        getTopBrands.execute().onEach { dataState ->
            loading.value = dataState.loading
            dataState.data?.let {
                topBrands.clear()
                topBrands.addAll(it)
            }
        }.launchIn(viewModelScope)
    }

}










