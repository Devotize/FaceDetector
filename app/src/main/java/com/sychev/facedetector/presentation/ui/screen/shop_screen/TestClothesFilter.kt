package com.sychev.facedetector.presentation.ui.screen.shop_screen

import com.sychev.facedetector.domain.Clothes

class TestClothesFilter() {

    var title: String = ""
    val genders: ArrayList<String> = arrayListOf()
    val itemCategories: ArrayList<String> = arrayListOf()
    val itemSubcategories: ArrayList<String> = arrayListOf()
    val brands: ArrayList<String> = arrayListOf()
    val itemSizes: ArrayList<String> = arrayListOf()
    val colors: ArrayList<String> = arrayListOf()
    val providers: ArrayList<String> = arrayListOf()
    var price: Pair<Int, Int> = Pair(0, 1000000000)
    var novice: Int = 0
    var popular: Int = 0
    var searchSize: Int = 1
    var fullTextQuery: String = ""

    var clothes: List<Clothes>? = null

    object Titles {
        val gender: String = "genders"
        val size: String = "size"
        val itemCategories: String = "item_categories"
        val itemSubcategories: String = "item_subcategories"
        val brands: String = "brands"
        val prices: String = "prices"
        val itemSizes: String = "item_sizes"
        val colors: String = "colours"
        val novice: String = "novice_flgs"
        val popularFlags: String = "popular_flgs"
        val providers: String = "providers"
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







