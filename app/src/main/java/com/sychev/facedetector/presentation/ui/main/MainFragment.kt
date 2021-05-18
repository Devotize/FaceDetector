package com.sychev.facedetector.presentation.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sychev.facedetector.domain.SavedScreenshot
import com.sychev.facedetector.presentation.ui.components.ScreenshotItem
import com.sychev.facedetector.utils.TAG
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
            viewModel.onTriggerEvent(MainEvent.GetAllScreenshots)
            setContent {
                val screenshotList = viewModel.screenshotList.value

                Column(
                    modifier = Modifier.fillMaxSize(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Library",
                        modifier = Modifier.padding(16.dp, 8.dp, 8.dp, 4.dp),
                        style = MaterialTheme.typography.h5
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .padding(16.dp, 0.dp, 16.dp, 8.dp),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        color = Color.LightGray
                    ){

                    }

                    screenshotList?.let {screenshotList ->
                        LazyVerticalGrid(cells = GridCells.Adaptive(200.dp)) {
                            itemsIndexed(screenshotList.reversed()){index: Int, savedScreenshot: SavedScreenshot ->
                                ScreenshotItem(
                                    savedScreenshot = savedScreenshot,
                                    onClick = {
                                        viewModel.onTriggerEvent(MainEvent.PerformGoogleSearch(context, savedScreenshot.celebName))
                                    }
                                )
                            }
                        }
                        }
                            
                    }
                }
            }
        }
    }










