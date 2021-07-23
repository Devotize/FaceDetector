package com.sychev.facedetector.presentation.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.sychev.facedetector.R
import com.sychev.facedetector.utils.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("ViewConstructor")
class ResizableRectangleView(
    context: Context,
) : View(context) {
    var points = arrayOfNulls<Point>(4)
    private var xCoordinates: List<Float> = listOf(0f,200f)
    private var yCoordinates: List<Float> = listOf(0f, 200f)

    private var widthPx = 0
    private var heightPx = 0
    private val windowManager =
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).also { wm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowsMetrics = wm.currentWindowMetrics
                val windowInsets = windowsMetrics.windowInsets
                val insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
                )
                val insetsWidth = insets.right + insets.left
                val insetsHeight = insets.top + insets.bottom
                val bounds = windowsMetrics.bounds
                widthPx = bounds.width() - insetsWidth
                heightPx = bounds.height() - insetsHeight
            } else {
                val size = Point()
                val display = wm.defaultDisplay
                display.getSize(size)
                widthPx = size.x
                heightPx = size.y
            }
        }



    var groupId = -1
    private val colorballs = ArrayList<ColorBall>()

    // array that holds the balls
    private var balID = 0

    // variable to know what ball is being dragged
    private val paint: Paint = Paint()
    //    private val myLeft = detectedObject?.boundingBox?.left ?: 400
//    private val myRight = detectedObject?.boundingBox?.right ?: 400
//    private val myTop = detectedObject?.boundingBox?.top ?: 400
//    private val myBottom= detectedObject?.boundingBox?.bottom ?: 400
    var rectLeft: Int = 0
    var rectRight: Int = 0
    var rectTop: Int = 0
    var rectBottom: Int = 0

    private fun setPoints() {
        isFocusable = true

        points[0] = Point()
        points[0]!!.x = rectLeft
        points[0]!!.y = rectTop
        points[1] = Point()
        points[1]!!.x = rectLeft
        points[1]!!.y = rectBottom
        points[2] = Point()
        points[2]!!.x = rectRight
        points[2]!!.y = rectBottom
        points[3] = Point()
        points[3]!!.x = rectRight
        points[3]!!.y = rectTop
        balID = 2
        groupId = 1
        // declare each ball with the ColorBall class
        points.forEachIndexed { index, pt ->
            val resDraw = when (index) {
                0 -> R.drawable.top_left_angle
                1 -> R.drawable.bottom_left_angle
                2 -> R.drawable.bottom_right_angle
                3 -> R.drawable.top_right_angle
                else -> R.drawable.clothes_default_icon
        }
            colorballs.add(ColorBall(context, resDraw, pt))
        }
    }

   private val _boxState = MutableStateFlow(Rect(rectLeft, rectTop, rectRight, rectBottom))
   val boxState = _boxState.asStateFlow()
    // the method that draws the balls
    override fun onDraw(canvas: Canvas) {
        if (points[3] == null) //point4 null when user did not touch and move on screen.
            return
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        left = points[0]!!.x
        top = points[0]!!.y
        right = points[0]!!.x
        bottom = points[0]!!.y
        for (i in 1 until points.size) {
            left = if (left > points[i]!!.x) points[i]!!.x else left
            top = if (top > points[i]!!.y) points[i]!!.y else top
            right = if (right < points[i]!!.x) points[i]!!.x else right
            bottom = if (bottom < points[i]!!.y) points[i]!!.y else bottom
        }
        paint.isAntiAlias = true
        paint.isDither = true
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = 5f

        //draw stroke
        paint.style = Paint.Style.STROKE
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.strokeWidth = 2f
        canvas.drawRect(
            (
                    left + colorballs[0].widthOfBall / 2).toFloat(), (
                    top + colorballs[0].widthOfBall / 2).toFloat(), (
                    right + colorballs[2].widthOfBall / 2).toFloat(), (
                    bottom + colorballs[2].widthOfBall / 2).toFloat(), paint
        )
        //fill the rectangle
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#66000000")
        paint.strokeWidth = 0f
        canvas.drawRect(
            (
                    left + colorballs[0].widthOfBall / 2).toFloat(), (
                    top + colorballs[0].widthOfBall / 2).toFloat(), (
                    right + colorballs[2].widthOfBall / 2).toFloat(), (
                    bottom + colorballs[2].widthOfBall / 2).toFloat(), paint
        )

        //draw the corners
        // draw the balls on the canvas
        paint.color = Color.BLUE
        paint.textSize = 18f
        paint.strokeWidth = 0f
        for (i in colorballs.indices) {
            val ball = colorballs[i]

                canvas.drawBitmap(
                    ball.bitmap, ball.x.toFloat(), ball.y.toFloat(),
                    paint
                )
//           canvas.drawText("" + (i + 1), ball.x.toFloat(), ball.y.toFloat(), paint)
        }
        rectLeft = if (left < right) left else right
        rectRight =  if (left < right) right else left
        rectTop = if (top < bottom) top else bottom
        rectBottom = if (top < bottom) bottom else top
//        Log.d(TAG, "onDraw: rectLeft: $rectLeft, rectRight: $rectRight, rectTop: $rectTop, rectBottom: $rectBottom")
        _boxState.value = Rect(rectLeft, rectTop, rectRight, rectBottom)

    }

    // events when touching the screen
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventaction = event.action
        val X = event.x.toInt()
        val Y = event.y.toInt()
        when (eventaction) {
            MotionEvent.ACTION_DOWN -> {
                //resize rectangle
                balID = -1
                groupId = -1
                var i = colorballs.size - 1
                while (i >= 0) {
                    val ball = colorballs[i]
                    val centerX = ball.x + ball.widthOfBall
                    val centerY = ball.y + ball.heightOfBall
                    paint.color = Color.CYAN
                    // calculate the radius from the touch to the center of the
                    // ball
                    val radCircle = Math
                        .sqrt(
                            ((centerX - X) * (centerX - X) + (centerY - Y)
                                    * (centerY - Y)).toDouble()
                        )
                    if (radCircle < ball.widthOfBall) {
                        balID = ball.iD
                        Log.d(TAG, "onTouchEvent: ActionDown: balID = $balID")
                        groupId = if (balID == 1 || balID == 3) {
                            2
                        } else {
                            1
                        }
                        invalidate()
                        break
                    }
                    invalidate()
                    i--
                }
            }
            MotionEvent.ACTION_MOVE -> if (balID > -1 && X > 5 && (X < widthPx - 40) && Y > 5 && (Y < heightPx - 40)) {
                // move the balls the same as the finger
                colorballs[balID].x = X
                colorballs[balID].y = Y
                paint.color = Color.CYAN
                if (groupId == 1) {
                    colorballs[1].x = colorballs[0].x
                    colorballs[1].y = colorballs[2].y
                    colorballs[3].x = colorballs[2].x
                    colorballs[3].y = colorballs[0].y
                } else {
                    colorballs[0].x = colorballs[1].x
                    colorballs[0].y = colorballs[3].y
                    colorballs[2].x = colorballs[3].x
                    colorballs[2].y = colorballs[1].y
                }
//                Log.d(TAG, "onTouchEvent: Moving rectangle X: $X, Y: $Y")
//                Log.d(TAG, "onTouchEvent: Moving rectangle widthPx: $widthPx, heightPx: $heightPx")
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        invalidate()
        return true
    }

    class ColorBall(context: Context, @DrawableRes resourceId: Int, var point: Point?) {
        val drawable: Drawable? = ContextCompat.getDrawable(context, resourceId)

        var bitmap: Bitmap = drawable!!.toBitmap(36,36)
        var iD: Int = count++.also {
            if (count == 4) count = 0
        }
        val widthOfBall: Int
            get() = bitmap.width
        val heightOfBall: Int
            get() = bitmap.height
        var x: Int
            get() = point!!.x
            set(x) {
                point!!.x = x
            }
        var y: Int
            get() = point!!.y
            set(y) {
                point!!.y = y
            }

        companion object {
            var count = 0
        }

    }

    private fun findLeftX(): Int? {
        val sorted = xCoordinates.sorted()
        Log.d(TAG, "findLeftX: sortedList = $sorted")
        return xCoordinates.minOrNull()?.toInt()
    }

    private fun findRightX(): Int? {
        return xCoordinates.maxOrNull()?.toInt()
    }

    private fun findTopY(): Int? {
        return yCoordinates.minOrNull()?.toInt()
    }

    private fun findBottomY(): Int? {
        return yCoordinates.maxOrNull()?.toInt()
    }

    private fun setBounds() {
        rectLeft = findLeftX() ?: 0
        rectRight = findRightX() ?: 0
        rectTop = findTopY() ?: 0
        rectBottom= findBottomY() ?: 0
    }

    fun setCoordinates(xCoordinates: List<Float>, yCoordinates: List<Float>) {
        this.xCoordinates = xCoordinates
        this.yCoordinates = yCoordinates
        setBounds()
        setPoints()
    }

}