package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.MotionEvent
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.customWidgets.DrawingView


class VirtualPointerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var pointerX = 300f
    var pointerY = 300f
    private val step = 15f
    private var isDrawing = false

    // Target DrawingView reference jahan drawing hogi
    var targetDrawingView: DrawingView? = null

    private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFD600")
        style = Paint.Style.FILL
    }
    private val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF1744")
        style = Paint.Style.FILL
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (pointerX == 300f && pointerY == 300f) {
            pointerX = (w / 2).toFloat()
            pointerY = (h / 2).toFloat()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(pointerX, pointerY, 20f, pointerPaint)
        canvas.drawCircle(pointerX, pointerY, 10f, innerPaint)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val w = width.toFloat()
        val h = height.toFloat()

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                pointerY = (pointerY - step).coerceIn(0f, h)
                moveDrawingIfNeeded()
                invalidate()
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                pointerY = (pointerY + step).coerceIn(0f, h)
                moveDrawingIfNeeded()
                invalidate()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                pointerX = (pointerX - step).coerceIn(0f, w)
                moveDrawingIfNeeded()
                invalidate()
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                pointerX = (pointerX + step).coerceIn(0f, w)
                moveDrawingIfNeeded()
                invalidate()
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                isDrawing = !isDrawing
                triggerDrawingAction(if (isDrawing) MotionEvent.ACTION_DOWN else MotionEvent.ACTION_UP)
                invalidate()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun moveDrawingIfNeeded() {
        if (isDrawing) {
            triggerDrawingAction(MotionEvent.ACTION_MOVE)
        }
    }

    private fun triggerDrawingAction(action: Int) {
        targetDrawingView?.let { drawingView ->
            // Coordinates ko drawing view ke relative convert karna
            val loc = IntArray(2)
            drawingView.getLocationOnScreen(loc)
            val canvasX = pointerX - loc[0]
            val canvasY = pointerY - loc[1]

            if (canvasX >= 0 && canvasX <= drawingView.width && canvasY >= 0 && canvasY <= drawingView.height) {
                val motionEvent = MotionEvent.obtain(0, 0, action, canvasX, canvasY, 0)
                drawingView.onTouchEvent(motionEvent)
            }
        }
    }
}