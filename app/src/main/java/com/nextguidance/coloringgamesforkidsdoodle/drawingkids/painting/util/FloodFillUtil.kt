package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import java.util.ArrayDeque

object FloodFillUtil {

    /**
     * Fills a bounded region with [replacementColor] starting from [touchPoint].
     * Stops automatically when hitting black/dark outline borders.
     */
    fun floodFill(
        bitmap: Bitmap,
        touchPoint: Point,
        replacementColor: Int
    ) {
        val width = bitmap.width
        val height = bitmap.height

        val startX = touchPoint.x
        val startY = touchPoint.y

        // Guard against out-of-bounds clicks
        if (startX !in 0 until width || startY !in 0 until height) return

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val startIndex = startY * width + startX
        val targetColor = pixels[startIndex]

        // If user taps an outline or the same color, do nothing
        if (isOutlineColor(targetColor) || targetColor == replacementColor) {
            return
        }

        // Fast queue storing packed 1D coordinates (avoids object allocation stutter)
        val queue = ArrayDeque<Int>(width * 4)
        queue.add(startIndex)
        pixels[startIndex] = replacementColor

        while (queue.isNotEmpty()) {
            val index = queue.removeFirst()
            val x = index % width
            val y = index / width

            // Check 4 adjacent neighbors: Left, Right, Up, Down
            checkNeighbor(x - 1, y, width, height, pixels, targetColor, replacementColor, queue)
            checkNeighbor(x + 1, y, width, height, pixels, targetColor, replacementColor, queue)
            checkNeighbor(x, y - 1, width, height, pixels, targetColor, replacementColor, queue)
            checkNeighbor(x, y + 1, width, height, pixels, targetColor, replacementColor, queue)
        }

        // Write the colored buffer back to the Bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    private inline fun checkNeighbor(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        pixels: IntArray,
        targetColor: Int,
        replacementColor: Int,
        queue: ArrayDeque<Int>
    ) {
        if (x in 0 until width && y in 0 until height) {
            val neighborIndex = y * width + x
            val currentColor = pixels[neighborIndex]

            // Fill if color matches target region and is not an outline
            if (currentColor == targetColor && !isOutlineColor(currentColor)) {
                pixels[neighborIndex] = replacementColor
                queue.add(neighborIndex)
            }
        }
    }

    /**
     * Determines whether a pixel represents a black or dark outline boundary.
     */
    private fun isOutlineColor(color: Int): Boolean {
        val alpha = Color.alpha(color)
        if (alpha < 50) return false // Transparent areas are not boundaries

        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        // If luminance is below 60, consider it a dark outline border
        val brightness = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
        return brightness < 60
    }
}