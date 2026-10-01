package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.helper


import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View

class TvRemoteCursorHelper(
    private val targetView: View,
    private val onDrawEvent: (action: Int, x: Float, y: Float) -> Unit
) {
    private var cursorX = 300f
    private var cursorY = 300f
    private var isDrawing = false
    private val step = 18f // Cursor movement speed

    init {
        targetView.isFocusable = true
        targetView.isFocusableInTouchMode = true
    }

    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    cursorY -= step
                    dispatchMotion(if (isDrawing) MotionEvent.ACTION_MOVE else MotionEvent.ACTION_HOVER_MOVE)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    cursorY += step
                    dispatchMotion(if (isDrawing) MotionEvent.ACTION_MOVE else MotionEvent.ACTION_HOVER_MOVE)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    cursorX -= step
                    dispatchMotion(if (isDrawing) MotionEvent.ACTION_MOVE else MotionEvent.ACTION_HOVER_MOVE)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    cursorX += step
                    dispatchMotion(if (isDrawing) MotionEvent.ACTION_MOVE else MotionEvent.ACTION_HOVER_MOVE)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                    isDrawing = !isDrawing
                    val action = if (isDrawing) MotionEvent.ACTION_DOWN else MotionEvent.ACTION_UP
                    dispatchMotion(action)
                    return true
                }
            }
        }
        return false
    }

    private fun dispatchMotion(action: Int) {
        onDrawEvent(action, cursorX, cursorY)
        targetView.invalidate()
    }

    fun getCursorCoordinates(): Pair<Float, Float> = Pair(cursorX, cursorY)
    fun isCursorDrawing(): Boolean = isDrawing
}