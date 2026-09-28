package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util


import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max

object SketchFilterUtil {

    /**
     * Converts any single colored bitmap into a black & white line-art sketch.
     */
    fun createSketch(sourceBitmap: Bitmap): Bitmap {
        val width = sourceBitmap.width
        val height = sourceBitmap.height

        val pixels = IntArray(width * height)
        sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        // 1. Calculate luminance (grayscale)
        val gray = IntArray(width * height)
        for (i in pixels.indices) {
            val c = pixels[i]
            val alpha = (c shr 24) and 0xFF
            if (alpha < 30) {
                gray[i] = 255 // Treat transparent areas as white paper
            } else {
                val r = (c shr 16) and 0xFF
                val g = (c shr 8) and 0xFF
                val b = c and 0xFF
                gray[i] = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            }
        }

        // 2. Detect outlines and color boundaries
        val outputPixels = IntArray(width * height)
        val edgeSensitivity = 22 // Lower = more detailed lines, Higher = cleaner bolder lines

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x

                // Keep existing black borders
                if (gray[index] < 50) {
                    outputPixels[index] = Color.BLACK
                    continue
                }

                // Check edge contrast against right and bottom neighbor
                var isEdge = false
                if (x < width - 1) {
                    val diffX = abs(gray[index] - gray[index + 1])
                    if (diffX > edgeSensitivity) isEdge = true
                }
                if (y < height - 1 && !isEdge) {
                    val diffY = abs(gray[index] - gray[index + width])
                    if (diffY > edgeSensitivity) isEdge = true
                }

                // Draw black line on edge, otherwise paper white
                outputPixels[index] = if (isEdge) Color.BLACK else Color.WHITE
            }
        }

        val sketchBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        sketchBitmap.setPixels(outputPixels, 0, width, 0, 0, width, height)
        return sketchBitmap
    }
}