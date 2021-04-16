package com.sychev.facedetector.domain

data class Person(
    val name: String,
    val googleSearch: String? = null,
    val instUrl: String? = null,
    val facebookUrl: String? = null,
    val kinopoiskUrl: String? = null
) {
}