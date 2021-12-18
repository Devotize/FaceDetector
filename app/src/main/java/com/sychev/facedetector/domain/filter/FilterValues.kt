package com.sychev.facedetector.domain.filter

data class FilterValues (
    var genders: List<String> = listOf(),
    var itemCategories: Pair<FilterValue, List<String>> = Pair(FilterValue(), listOf()),
    var itemSubcategories: Pair<FilterValue, List<String>> = Pair(FilterValue(), listOf()),
    var brands: Pair<FilterValue, List<String>> = Pair(FilterValue(), listOf()),
    var itemSizes: List<String> = listOf(),
    var colors: Pair<FilterValue, List<ColorsFilterValue>> = Pair(FilterValue(), listOf()),
    var providers: List<Provider> = listOf(),
    var price: Price = Price()
) {
    object Constants{
        object Gender{
            val male = "мужской"
            val female = "женский"
        }
        object Novice {
            val default = 0
            val new = 1
        }
        object Popular {
            val default = 0
            val popular = 1
        }
    }
}


data class ColorsFilterValue(
    val colorName: String,
    val colorHex: String,
)

data class FilterValue(
    val id: String = "",
    val name: String = "",
)

data class Price(
    var min: Int = 0,
    var max: Int = 10000000,
)
data class Provider(
    val id: String,
    val displayName: String,
)




