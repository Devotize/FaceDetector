package com.sychev.facedetector.presentation.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.presentation.ui.components.ScreenshotItem
import com.sychev.facedetector.utils.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            viewModel.onTriggerEvent(MainEvent.GetAllScreenshots)
            setContent {
                val screenshotList = viewModel.screenshotList.value

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    screenshotList?.let {screenshotList ->
                        LazyRow(
                            content = {
                            itemsIndexed(screenshotList.reversed()){index: Int, savedScreenshot: SavedScreenshot ->
                                ScreenshotItem(savedScreenshot = savedScreenshot)
                            }
                        }) 
                    }
                }
            }
        }
    }
}









