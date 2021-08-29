package com.sychev.facedetector.presentation.ui.screen.clothes_detail

sealed class ClothesDetailEvent {
    class SearchSimilarClothesEvent(val query: String, val size: Int): ClothesDetailEvent()
}