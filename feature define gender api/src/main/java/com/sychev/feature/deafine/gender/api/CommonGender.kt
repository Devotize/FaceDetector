package com.sychev.feature.deafine.gender.api

data class CommonGender(
    val value: Gender
)

enum class Gender(val value: String) {
    MALE("мужской"), FEMALE("женский")
}