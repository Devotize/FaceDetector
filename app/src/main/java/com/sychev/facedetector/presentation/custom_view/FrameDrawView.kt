package com.sychev.facedetector.presentation.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.sychev.facedetector.R


class FrameDrawView(context: Context): View(context) {

    private var currentX = 0f
    private var currentY = 0f
    private val painColor = ContextCompat.getColor(context, R.color.grey_dark)
    private val paint = Paint().apply {
        color = painColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeWidth = 12f
    }
    private val path = Path()
    val touchedCoordinatesX = mutableListOf<Float>()
    val touchedCoordinatesY = mutableListOf<Float>()


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f,0f,width.toFloat(),height.toFloat(), paint)
        canvas?.drawPath(path, paint)

//        Log.d(TAG, "onDraw: X's: $touchedCoordinatesX, \n Y's: $touchedCoordinatesY")

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
            }
        }
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        path.moveTo(x, y)
        currentCoordinatesChanged(x, y)
    }

    private fun touchMove(x: Float, y: Float) {
        path.quadTo(currentX, currentY, (x + currentX) / 2, (y + currentY) / 2)
        currentCoordinatesChanged(x,y)
        refreshDrawableState()
        invalidate()
    }

    private fun currentCoordinatesChanged(newX: Float, newY: Float) {
        currentX = newX
        currentY = newY
        touchedCoordinatesX.add(currentX)
        touchedCoordinatesY.add(currentY)
    }

    private fun touchUp() {
        path.reset()
        touchedCoordinatesX.clear()
        touchedCoordinatesY.clear()
        invalidate()
    }

}



