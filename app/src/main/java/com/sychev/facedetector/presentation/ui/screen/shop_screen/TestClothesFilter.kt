package com.sychev.facedetector.presentation.ui.screen.shop_screen

import com.sychev.facedetector.domain.Clothes

data class TestClothesFilter(
    var title: String = "",
    val genders: ArrayList<String> = arrayListOf(),
    val itemCategories: ArrayList<String> = arrayListOf(),
    val itemSubcategories: ArrayList<String> = arrayListOf(),
    val brands: ArrayList<String> = arrayListOf(),
    val itemSizes: ArrayList<String> = arrayListOf(),
    val colors: ArrayList<String> = arrayListOf(),
    val providers: ArrayList<String> = arrayListOf(),
    var price: Pair<Int, Int> = Pair(0, 1000000000),
    var novice: Int = 0,
    var popular: Int = 0,
    var searchSize: Int = 1,
    var fullTextQuery: String = "",

    var clothes: List<Clothes>? = null
) {
    object Titles {
        val gender: String = "gender"
        val size: String = "size"
        val itemCategories: String = "category"
        val itemSubcategories: String = "subcategory"
        val brands: String = "brand"
        val prices: String = "price"
        val itemSizes: String = "size"
        val colors: String = "colour"
        val novice: String = "novice_flg"
        val popularFlags: String = "popular_flg"
        val providers: String = "provider"
        val fullTextQuery: String = "full_text_query"
    }

    object Filters {
        val defaultFilters = listOf<TestClothesFilter>(
            TestClothesFilter().apply {
                title = "Clothes up to 500 ₽"
                price = Pair(0, 500)
            },
            TestClothesFilter().apply {
                title = "New of the week"
                novice = 1
            },
            TestClothesFilter().apply {
                title = "Popular"
                popular = 1
            },
            TestClothesFilter().apply {
                title = "Clothes up to 1000 ₽"
                price = Pair(500, 1000)
            },
        )
    }

    }






