package com.sychev.facedetector.presentation.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sychev.facedetector.domain.DetectedClothes
import com.sychev.facedetector.presentation.activity.MainActivity
import com.sychev.facedetector.presentation.ui.components.AppTopBar
import com.sychev.facedetector.presentation.ui.components.ClothesItem
import com.sychev.facedetector.presentation.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainFragmentViewModel by viewModels()

    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                viewModel.onTriggerEvent(MainEvent.GetAllDetectedClothes)

                val query = viewModel.query.value
                val detectedClothesList = viewModel.detectedClothesList

                AppTheme {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AppTopBar(
                            query = query,
                            onQueryChange = viewModel::onQueryChange,
                            onStartAssistant = {
                                viewModel.onTriggerEvent(MainEvent.LaunchDetector(activity as MainActivity))
                            }
                        )
                        LazyColumn(
                            modifier = Modifier,
                        ) {
                            itemsIndexed(detectedClothesList) { index: Int, item: DetectedClothes ->
                                ClothesItem(
                                    detectedClothes = item,
                                    onAddToFavoriteClick = {
                                        viewModel.onTriggerEvent(
                                            MainEvent.AddToFavoriteDetectedClothesEvent(
                                                it
                                            )
                                        )
                                    },
                                    onRemoveFromFavoriteClick = {
                                        viewModel.onTriggerEvent(
                                            MainEvent.RemoveFromFavoriteDetectedClothesEvent(
                                                it
                                            )
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}










