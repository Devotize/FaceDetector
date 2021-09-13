package com.sychev.facedetector.presentation.ui.screen.shop_screen

import com.sychev.facedetector.domain.Clothes

open class ClothesFilters {

    open var title: String = ""
    open var gender: ArrayList<Gender> = ArrayList()
    open var size: Int = 1
    open var itemCategories: ArrayList<ItemCategories> = ArrayList()
    open var itemSubcategories: ArrayList<ItemSubcategories> = ArrayList()
    open var brands: ArrayList<Brands> = ArrayList()
    open var prices: ArrayList<Int> = arrayListOf(0, 1000000000)
    open var itemSizes: ArrayList<ItemSizes> = ArrayList()
    open var colors: ArrayList<ClothesColors> = ArrayList()
    open var novice: ArrayList<Novice> = ArrayList()
    open var popularFlags: ArrayList<PopularFlags> = ArrayList()
    open var providers: ArrayList<Providers> = ArrayList()
    open var fullTextQuery: String? = null

    var clothes: List<Clothes>? = null

    enum class Gender(val title: String) {
        FEMALE("женский"),
        MALE("мужской")
    }

    enum class ItemCategories(val title: String) {
        BLOUSE("блузка"),
        SHIRT("рубашка"),
        PANTS("брюки"),
        TURTLENECK("водолазка"),
        JUMPER("джемпер"),
        CARDIGAN("кардиган"),
        T_SHIRT("футболка"),
        SWEATER("свитер"),
    }

    enum class ItemSubcategories(val title: String) {
        BLOUSE("блузка"),
        SHIRT("рубашка"),
        PANTS("брюки"),
        TURTLENECK("водолазка"),
        JUMPER("джемпер"),
        CARDIGAN("кардиган"),
        T_SHIRT("футболка"),
        SWEATER("свитер"),
    }

    enum class Brands(val title: String) {
        GLORIA_JEANS("Gloria Jeans"),
        CONCEPT_CLUB("Concept Club"),
        MANGO("Mango"),
        ZARINA("Zarina"),
        LESSOTICO("Lussotico"),
        MORGAN("Morgan"),
        COLIN_S("Colin's"),
        RAIMAXX("RaiMaxx"),
        PRIMA_LINEA("Prima Linea"),
    }

    enum class ItemSizes(val size: Int) {
        FORTY_EIGHT(48),
        FORTY_NINE(49),
        FIFTY(50),
        FIFTY_ONE(51),
        FIFTY_TWO(52),
    }

    enum class ClothesColors(val title: String) {
        RED("красный"),
        YELLOW("желтый"),
        BLUE("синий"),
        BLACK("черный")
    }

    enum class Novice(val index: Int) {
        NEW(1),
        DEFAULT(0),
    }

    enum class PopularFlags(val index: Int) {
        POPULAR(1),
        DEFAULT(0),
    }

    enum class Providers(val title: String) {
        LAMODA("lamoda"),
        WILDBERRIES("wildberries")
    }

    fun isDefaultFilter(): Boolean {
        return this is DefaultClothesFilters
    }

    abstract class DefaultClothesFilters(): ClothesFilters()

    companion object{
        fun defaultFilters(): List<ClothesFilters> {
            return listOf<ClothesFilters>(
                object : DefaultClothesFilters() {
                    override var title: String = "Clothes up to 500 ₽"
                    override var prices: ArrayList<Int> = arrayListOf<Int>(0, 500)
                },
                object : DefaultClothesFilters() {
                    override var title: String = "New of the week"
                    override var novice: ArrayList<Novice> = arrayListOf(Novice.NEW)
                },
                object : DefaultClothesFilters() {
                    override var title: String = "Popular"
                    override var popularFlags: ArrayList<PopularFlags> = arrayListOf(PopularFlags.POPULAR)
                },
                object : DefaultClothesFilters() {
                    override var title: String = "Clothes up to 1000 ₽"
                    override var prices: ArrayList<Int> = arrayListOf(500, 1000)
                }
            )
        }
    }

}















