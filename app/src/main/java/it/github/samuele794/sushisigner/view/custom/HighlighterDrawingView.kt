package it.github.samuele794.sushisigner.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import it.github.samuele794.sushisigner.R


class HighlighterDrawingView constructor(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    private var drawStartListener: (() -> Unit)? = null
    private var mPaint = Paint()
    private var mPath: Path
    private val pointList = arrayListOf<Point>()

    private var drawEndListener: ((pointList: List<Point>) -> Unit)? = null

    var isDrawEnabled: Boolean = false
    var isDrawClosed: Boolean = false

    init {
        mPaint.apply {
            color = context.getColor(R.color.green_A400)
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = resources.getDimension(R.dimen.line_width)
            alpha = 80
        }
        mPath = Path()
    }

    fun setOnDrawEndlistener(listener: (listener: List<Point>) -> Unit) {
        drawEndListener = listener
    }

    fun setOnDrawStartlistener(listener: () -> Unit) {
        drawStartListener = listener
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)

        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isDrawEnabled && !isDrawClosed) {

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mPath.reset()
                    pointList.clear()
                    drawStartListener?.invoke()
                    mPath.moveTo(event.x, event.y)

                }
                MotionEvent.ACTION_MOVE -> {
                    pointList.add(Point(event.x.toInt(), event.y.toInt()))
                    mPath.lineTo(event.x, event.y)
                }

                MotionEvent.ACTION_UP -> {
//                    mPath.close()
                    invalidate()
                    if (pointList.isNotEmpty()) {
                        isDrawClosed = true
                        drawEndListener?.invoke(pointList)
                    }
                }
            }

            invalidate()
        }

        return true
    }

    fun clearDraw() {
        mPath.reset()
        pointList.clear()
        invalidate()
    }

    fun enableDraw() {
        isDrawEnabled = true
        isDrawClosed = false
    }
}