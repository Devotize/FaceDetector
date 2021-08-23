package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@ExperimentalPagerApi
@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    activeColor: Color,
    inactiveColor: Color,
    activeWidth: Dp = 8.dp,
    activeHeight: Dp = 8.dp,
    inactiveWidth: Dp = activeWidth,
    inactiveHeight: Dp = activeHeight,
    shape: Shape = CircleShape
) {
    val pages = Array<Any>(pagerState.pageCount){

    }
    val spacing = inactiveWidth
    val indicatorActiveColor = remember{ mutableStateOf(activeColor)}
    if (pagerState.targetPage == null) {
        indicatorActiveColor.value = activeColor
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            pages.forEachIndexed { index, any ->
                Box(
                    modifier = Modifier
                        .width(if (index == pagerState.currentPage) activeWidth else inactiveWidth)
                        .height(if (index == pagerState.currentPage) activeHeight else inactiveHeight)
                        .background(
                            color = if (index == pagerState.currentPage) indicatorActiveColor.value else inactiveColor,
                            shape = shape,
                        ),

                    ) {

                }
            }
        }
        pagerState.targetPage?.let{
            indicatorActiveColor.value = inactiveColor
            Box(
                Modifier
                    .offset {
                        val scrollPosition = (pagerState.currentPage + pagerState.currentPageOffset)
                            .coerceIn(0f, (pagerState.pageCount - 1).toFloat())
                        IntOffset(
                            x = ((spacing + inactiveWidth) * scrollPosition).roundToPx(),
                            y = 0
                        )
                    }
                    .size(width = activeWidth, height = activeHeight)
                    .background(
                        color = activeColor,
                        shape = shape,
                    )
            )
        }
    }


}
