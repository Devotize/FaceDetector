package com.sychev.facedetector.presentation.ui.screen.shop

import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.domain.filter.Price

data class ClothesFilter (
    var title: String = "",
    val genders: ArrayList<String> = arrayListOf(),
    val itemCategories: ArrayList<String> = arrayListOf(),
    val itemSubcategories: ArrayList<String> = arrayListOf(),
    val brands: ArrayList<String> = arrayListOf(),
    val itemSizes: ArrayList<String> = arrayListOf(),
    val colors: ArrayList<String> = arrayListOf(),
    val providers: ArrayList<String> = arrayListOf(),
    var price: Price = Price(),
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
        val defaultFilters = listOf<ClothesFilter>(
            ClothesFilter().apply {
                title = "Одежда до 500 ₽"
                price = Price(0, 500)
            },
            ClothesFilter().apply {
                title = "Новинки этой недели"
                novice = 1
            },
            ClothesFilter().apply {
                title = "Популярно сегодня"
                popular = 1
            },
            ClothesFilter().apply {
                title = "Одежда до 1000 ₽"
                price = Price(500, 1000)
            },
        )
    }

    }







