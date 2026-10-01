package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util


import android.view.View

object ViewFocusAnimator {
    fun enableFocusAnimation(view: View) {
        view.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) 1.12f else 1.0f
            val elevation = if (hasFocus) 16f else 4f

            v.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(180)
                .start()

            v.elevation = elevation
        }
    }
}