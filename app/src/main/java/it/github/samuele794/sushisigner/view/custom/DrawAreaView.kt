package it.github.samuele794.sushisigner.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import it.github.samuele794.sushisigner.R

class DrawAreaView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    init {
        isFocusable = true
        mPaint.apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            mPaint.strokeJoin = Paint.Join.ROUND
            color = context.getColor(R.color.green_A400)

            strokeWidth = resources.getDimension(R.dimen.line_width)

            alpha = 80
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val mEvent = when (event.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                path.setLastPoint(event.x, event.y)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                true
            }

            MotionEvent.ACTION_UP -> {
                true
            }
            else -> true
        }

        invalidate()

        return mEvent
    }
}