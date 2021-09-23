package com.sychev.facedetector.domain.filter

import com.sychev.facedetector.presentation.ui.navigation.NavigationManager

data class FilterValues (
    var genders: List<String> = listOf(),
    var itemCategories: List<String> = listOf(),
    var itemSubcategories: List<String> = listOf(),
    var brands: List<String> = listOf(),
    var itemSizes: List<String> = listOf(),
    var colors: List<String> = listOf(),
    var providers: List<String> = listOf(),
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
