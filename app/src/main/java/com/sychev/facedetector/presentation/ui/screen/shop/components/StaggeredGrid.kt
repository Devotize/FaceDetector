package com.sychev.facedetector.presentation.ui.screen.shop.components

import android.util.Log
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import com.sychev.facedetector.utils.TAG
import kotlin.math.max


@Composable
private fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 } // Keep track of the width of each row
        val rowHeights = IntArray(rows) { 0 } // Keep track of the height of each row

        // Don't constrain child views further, measure them with given constraints
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
            ?: constraints.minWidth
        // Grid's height is the sum of each row
        val height = rowHeights.sum().coerceIn(constraints.minHeight, constraints.maxHeight)

        // y co-ord of each row
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }
        layout(width, height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.place(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
private fun StaggeredHorizontalGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constrains ->
        val screenMaxWidth = constrains.maxWidth
        val screenMaxHeight = constrains.maxHeight
        val rowPlaceablesArrayList: ArrayList<ArrayList<Placeable>> = ArrayList()
        val coordinatesArrayList: ArrayList<ArrayList<Pair<Int, Int>>> = ArrayList()
        rowPlaceablesArrayList.add(arrayListOf())
        coordinatesArrayList.add(arrayListOf())
        var rowIndex = 0
        var width = 0
        var height = 0
        var x = 0
        var y = 0
        measurables.forEach {
            val placeable = it.measure(constrains)
            width += placeable.width
            if (width >= screenMaxWidth) {
                rowIndex++
                width = 0
                rowPlaceablesArrayList.add(arrayListOf())
                coordinatesArrayList.add(arrayListOf())
                x = 0
                y += placeable.height
            }
            height = placeable.height * (rowIndex + 1)

            rowPlaceablesArrayList[rowIndex].add(placeable)
            coordinatesArrayList[rowIndex].add(Pair(x, y))
            x += placeable.width
        }
        layout(width, height) {
            for (i in 0..rowIndex) {
                rowPlaceablesArrayList[i].forEachIndexed { index, placeable ->
                    placeable.place(
                        x = coordinatesArrayList[i][index].first,
                        y = coordinatesArrayList[i][index].second
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableStaggeredHorizontalGrid(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    expandButton: @Composable () -> Unit = {
        Icon(imageVector = Icons.Default.Expand, contentDescription = null)
    },
    content: @Composable () -> Unit,
) {
    var isMoreThenOneRow = false
    Layout(
        modifier = modifier,
        content = {
            content()
            expandButton()
        }
    ) { measurables, constrains ->
        if (measurables.size == 1) {
            return@Layout layout(0, 0) {

            }
        }
        val screenMaxWidth = constrains.maxWidth
        val screenMaxHeight = constrains.maxHeight
        val rowPlaceablesArrayList: ArrayList<ArrayList<Placeable>> = ArrayList()
        val coordinatesArrayList: ArrayList<ArrayList<Pair<Int, Int>>> = ArrayList()
        rowPlaceablesArrayList.add(arrayListOf())
        coordinatesArrayList.add(arrayListOf())
        var rowIndex = 0
        var width = 0
        var height = 0
        var x = 0
        var y = 0
        run lit@{
            measurables.forEach {
                try {
                    val placeable = it.measure(constrains)
                    width += placeable.width
                    if (width >= screenMaxWidth - 300) {
                        Log.d(TAG, "ExpandableStaggeredHorizontalGrid: moreThenOneRow")
                        isMoreThenOneRow = true
                        if (!isExpanded) {
                            rowPlaceablesArrayList[rowIndex].add(
                                measurables.last().measure(constrains)
                            )
                            coordinatesArrayList[rowIndex].add(Pair(x, y))
                            return@lit
                        }
                        rowIndex++
                        width = 0
                        rowPlaceablesArrayList.add(arrayListOf())
                        coordinatesArrayList.add(arrayListOf())
                        x = 0
                        y += placeable.height
                    }
                    height = placeable.height * (rowIndex + 1)

                    rowPlaceablesArrayList[rowIndex].add(placeable)
                    coordinatesArrayList[rowIndex].add(Pair(x, y))
                    x += placeable.width
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        layout(width, height) {
            for (i in 0..rowIndex) {
                rowPlaceablesArrayList[i].forEachIndexed { index, placeable ->
//                    Log.d(TAG, "ExpandableStaggeredHorizontalGrid: placing placeable with rowIndex: $i")
                    //check if need to add expand button
                    if (i == rowIndex && index == rowPlaceablesArrayList[i].lastIndex && !isMoreThenOneRow) {
                        Log.d(TAG, "ExpandableStaggeredHorizontalGrid: do not add expand button")
                        //do not add expand button
                        return@forEachIndexed
                    } else {
                        placeable.place(
                            x = coordinatesArrayList[i][index].first,
                            y = coordinatesArrayList[i][index].second
                        )
                    }

                }
            }
        }
    }
}