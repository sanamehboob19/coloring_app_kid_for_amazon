package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import android.widget.Toast
import androidx.core.graphics.toColorInt
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.LabelInfo
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.Template
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt





data class RippleEffect(
    val x: Float,
    val y: Float,
    val color: Int,
    var progress: Float = 0f,      // 0f → 1f
    var alpha: Float = 1f,
    var animator: ValueAnimator? = null
)


class ColoringView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var onColorLongClickListener: OnColorLongClickListener? = null
    var colorProgressListener: OnColorProgressListener? = null
    var onZoomChanged: ((isZoomed: Boolean) -> Unit)? = null
    private val baseMatrix = Matrix()

    // ─────────────────────────────────────────────────────────────
    // DATA CLASSES
    // ─────────────────────────────────────────────────────────────

    data class GroupItem(
        val id: Int,
        val bitmap: Bitmap,
        val offsetX: Float,
        val offsetY: Float,
        val bounds: RectF, // RectF(offsetX, offsetY, offsetX + bitmap.width, offsetY + bitmap.height)
        val label: LabelInfo,
        val tempColor: Int,
        val isStroke: Boolean,
        var animProgress: Float = 0f,
        var touchPoint: PointF? = null,
        var blinkAlpha: Int = 0
    )

    private val groups = mutableListOf<GroupItem>()
    private val hidden = mutableSetOf<Int>()
    private val colorId = mutableListOf<Pair<Int, Int>>()
    private var selectedColor = Color.BLACK

    private var bgBitmap: Bitmap? = null
    private var bgDestRect = RectF()
    private var bgSrcRect = Rect()

    private val ripples = mutableListOf<RippleEffect>()
    private val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG)


    private var lastFlingX = 0
    private var lastFlingY = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val drawMatrix = Matrix()
    private val inverseMatrix = Matrix()
    private var currentScale = 1f
    private var baseScale = 1f
    private val maxScale = 15f

    private var contentWidth = 0f
    private var contentHeight = 0f

    private val scroller = OverScroller(context)
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, GestureListener())

    // ─────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────

    fun getColorListWithIds(): List<Pair<Int, Int>> {
        return colorId.filter { it.second != Color.BLACK }
    }

    fun isColorSelected(color: Int): Boolean {

        return color == selectedColor
    }

    fun getSelectedColor() = selectedColor
    fun setSelectedColor(selectedColor: Int) {
        this.selectedColor = selectedColor
        invalidate()
    }

    fun setGroupedTemplate(templates: List<Template>) {
        colorId.clear()
        groups.clear()
        var fillableIdCounter = 1
        val allBounds = RectF()

        templates.reversed().forEach { template ->
            val colorInt = if (template.isStroke) Color.BLACK else template.color.toColorInt()

            if (!template.isStroke) {
                colorId.add(Pair(fillableIdCounter++, colorInt))
            }

            template.items.forEach { item ->
                val itemBounds = RectF(
                    item.offsetX,
                    item.offsetY,
                    item.offsetX + item.bitmap.width,
                    item.offsetY + item.bitmap.height
                )
                allBounds.union(itemBounds)

                groups.add(
                    GroupItem(
                        id = item.id,
                        bitmap = item.bitmap,
                        offsetX = item.offsetX,
                        offsetY = item.offsetY,
                        bounds = itemBounds,
                        label = item.label,
                        tempColor = colorInt,
                        isStroke = template.isStroke
                    )
                )
            }
        }

        selectedColor = colorId.firstOrNull()?.second ?: Color.BLACK
        contentWidth = allBounds.right
        contentHeight = allBounds.bottom
        recomputeBgRects()
        updateBase()
        invalidate()
        val isAllFilled = getColorFilled()

        if (isAllFilled) {
            colorProgressListener?.onComplete()
        }
    }

    fun setBackgroundDrawableImage(drawable: Drawable) {
        bgBitmap = (drawable as BitmapDrawable).bitmap
        recomputeBgRects()
        invalidate()
    }

    fun getFilledIds(): List<Int> = hidden.toList()

    fun getColorFilled(): Boolean {
        val target = groups.find { !it.isStroke && !hidden.contains(it.id) && it.tempColor == selectedColor }
        return target == null
    }

    fun setFilledIds(filledIds: List<Int>) {
        if (filledIds.isEmpty()) return
        filledIds.forEach { id ->
            // Find group by id and mark as hidden (filled)
            groups.find { it.id == id }?.let { group ->
                hidden.add(group.id)
            }
        }
        invalidate()
        val isAllFilled = getColorFilled()

        if (isAllFilled) {
            colorProgressListener?.onComplete()
        }
    }

    fun setBackgroundBitmapImage(bitmap: Bitmap) {
        bgBitmap = bitmap
        recomputeBgRects()
        invalidate()
    }

    // ─────────────────────────────────────────────────────────────
    // DRAWING
    // ─────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        // SAFETY: If the view is detached or canvas is invalid, don't draw
        if (groups.isEmpty()) return

        super.onDraw(canvas)
        canvas.save()
        canvas.concat(drawMatrix)

        // 1. Draw Background
        bgBitmap?.let { canvas.drawBitmap(it, bgSrcRect, bgDestRect, null) }

        for (group in groups) {
            // Culling Check for performance
            if (!isWithinViewport(group.bounds)) continue

            val isFilled = hidden.contains(group.id)
            val isSelected = group.tempColor == selectedColor

            when {
                // --- STATE: STROKE ---
                group.isStroke -> {
//                    canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, null)
                }

                // --- STATE: FILLED (with Animation) ---
                isFilled -> {
                    if (group.animProgress < 1.0f && group.touchPoint != null) {
                        // Reuse a single offscreen per group, or use canvas layers
                        val layerId = canvas.saveLayer(null, null)  // ← cheaper than new Bitmap

                        canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, null)

                        val holePaint = Paint().apply {
                            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                        }
                        val holeRadius = max(group.bounds.width(), group.bounds.height()) * 1.5f * group.animProgress
                        canvas.drawCircle(group.touchPoint!!.x, group.touchPoint!!.y, holeRadius, holePaint)

                        canvas.restoreToCount(layerId)  // composites the layer with hole onto main canvas
                    }
                }

                isSelected -> {
                    canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, null)
                }

                // --- STATE: UNSELECTED (White Mask) ---
                else -> {
                    // This makes the bitmap area appear as a solid white shape
                    paint.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, paint)
                    paint.colorFilter = null
                }
            }

            // 4. Highlight/Blink Logic
            if (group.blinkAlpha > 0) {
                val blinkPaint = Paint().apply {
                    color = Color.YELLOW
                    alpha = group.blinkAlpha
                }
                canvas.drawRect(group.bounds, blinkPaint)
            }
        }

        drawLabels(canvas)
        drawRipples(canvas, currentScale)
        canvas.restore()
    }

    data class RippleEffect(
        val x: Float,
        val y: Float,
        val color: Int,
        var progress: Float = 0f,
        var animator: ValueAnimator? = null
    )

    private fun drawRipples(canvas: Canvas, currentScale: Float) {
        val toRemove = mutableListOf<RippleEffect>()

        for (ripple in ripples) {
            if (ripple.progress >= 1f) {
                toRemove.add(ripple)
                continue
            }

            val p = ripple.progress
            val cx = ripple.x
            val cy = ripple.y
            val s = (2f / currentScale).coerceIn(0.5f, 10f)

            // ─────────────────────────────────────────────────────
            // CENTER DOT
            // ─────────────────────────────────────────────────────
            if (p < 0.45f) {
                val dotP = p / 0.45f
                val dotRadius = when {
                    dotP < 0.3f -> 8f * s * (dotP / 0.3f)
                    dotP < 0.5f -> 8f * s + 2f * s * ((dotP - 0.3f) / 0.2f)
                    else -> 10f * s * (1f - (dotP - 0.5f) / 0.5f)
                }
                val dotAlpha = when {
                    dotP < 0.6f -> 255
                    else -> ((1f - (dotP - 0.6f) / 0.4f) * 255).toInt()
                }

                if (dotRadius > 0f && dotAlpha > 0) {
                    // Shadow
                    ripplePaint.style = Paint.Style.FILL
                    ripplePaint.color = Color.BLACK
                    ripplePaint.alpha = (dotAlpha * 0.2f).toInt()
                    canvas.drawCircle(cx + 1.5f * s, cy + 1.5f * s, dotRadius + 1f * s, ripplePaint)

                    // White border
                    ripplePaint.color = Color.WHITE
                    ripplePaint.alpha = dotAlpha
                    canvas.drawCircle(cx, cy, dotRadius + 2.5f * s, ripplePaint)

                    // Color fill
                    ripplePaint.color = ripple.color
                    ripplePaint.alpha = dotAlpha
                    canvas.drawCircle(cx, cy, dotRadius, ripplePaint)

                    // Specular
                    ripplePaint.color = Color.WHITE
                    ripplePaint.alpha = (dotAlpha * 0.55f).toInt()
                    canvas.drawCircle(
                        cx - dotRadius * 0.28f,
                        cy - dotRadius * 0.28f,
                        dotRadius * 0.32f,
                        ripplePaint
                    )
                }
            }

            // ─────────────────────────────────────────────────────
            // PARTICLES
            // ─────────────────────────────────────────────────────
            if (p < 0.9f) {
                val particleP = p / 0.9f
                val eased = 1f - (1f - particleP) * (1f - particleP)
                val particleAlpha = ((1f - particleP) * 255).toInt()
                val particleSize = (4f * (1f - particleP) + 0.5f) * s  // scaled

                val angles = listOf(0f, 58f, 120f, 183f, 245f, 302f)
                val distances = listOf(28f, 22f, 30f, 24f, 32f, 23f)

                for ((idx, angle) in angles.withIndex()) {
                    val rad = Math.toRadians(angle.toDouble())
                    val dist = distances[idx] * s * eased  // scaled

                    val px = cx + dist * Math.cos(rad).toFloat()
                    val py = cy + dist * Math.sin(rad).toFloat() + 18f * s * particleP * particleP  // scaled gravity

                    // White border
                    ripplePaint.style = Paint.Style.FILL
                    ripplePaint.color = Color.WHITE
                    ripplePaint.alpha = particleAlpha
                    canvas.drawCircle(px, py, particleSize + 1.5f * s, ripplePaint)

                    // Color fill
                    ripplePaint.color = ripple.color
                    ripplePaint.alpha = particleAlpha
                    canvas.drawCircle(px, py, particleSize, ripplePaint)
                }
            }
        }

        ripples.removeAll(toRemove)
    }

    private fun startRipple(x: Float, y: Float, color: Int) {
        val ripple = RippleEffect(x = x, y = y, color = color)

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600
            interpolator = LinearInterpolator()
            addUpdateListener {
                ripple.progress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    ripples.remove(ripple)
                    ripple.animator = null
                }
            })
            ripple.animator = this
            start()
        }

        ripples.add(ripple)
    }

    private fun drawLabels(canvas: Canvas) {
        // Optimization: If currentScale is very low (zoomed out), don't even loop
        if (currentScale < (baseScale * 0.11f)) return

        for (group in groups) {
            if (group.isStroke || hidden.contains(group.id)) continue

            // screenRadius is the actual size on the physical screen
            val screenRadius = group.label.radius * currentScale

            // Threshold: 15f is usually enough to read a single digit.
            // We remove the screenRadius > 200f check so they don't disappear when very close.
            if (screenRadius < 20f) continue

            val number = colorId.find { it.second == group.tempColor }?.first ?: continue
            val numberStr = number.toString()

            // Text size calculation:
            // We want the text to fill roughly 80% of the label's radius
            val textSize = group.label.radius * 0.8f
            fillPaint.textSize = textSize
            fillPaint.color = Color.parseColor("#444444") // Dark grey for better contrast
            fillPaint.alpha = 255

            // Culling: Only draw text if the label center is inside the current screen view
            // (Optional but good for performance)
            if (!isPointInViewport(group.label.centerX, group.label.centerY)) continue

            val yPos = group.label.centerY - (fillPaint.descent() + fillPaint.ascent()) / 2f
            canvas.drawText(numberStr, group.label.centerX, yPos, fillPaint)
        }
    }

    // Helper to check if a point is on screen
    private fun isPointInViewport(cx: Float, cy: Float): Boolean {
        val pts = floatArrayOf(cx, cy)
        drawMatrix.mapPoints(pts)
        return pts[0] >= 0 && pts[0] <= width && pts[1] >= 0 && pts[1] <= height
    }

    private fun notifyZoomChanged() {
        val isModified = !drawMatrix.isIdenticalTo(baseMatrix)
        onZoomChanged?.invoke(isModified)
    }

    private fun Matrix.isIdenticalTo(other: Matrix, tolerance: Float = 0.5f): Boolean {
        val a = FloatArray(9)
        val b = FloatArray(9)
        this.getValues(a)
        other.getValues(b)
        return a.zip(b.toList()).all { (x, y) -> abs(x - y) < tolerance }
    }

    private fun Float.approximatelyEquals(other: Float, tolerance: Float = 0.01f): Boolean {
        return abs(this - other) < tolerance
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val dx = scroller.currX - lastFlingX
            val dy = scroller.currY - lastFlingY
            lastFlingX = scroller.currX
            lastFlingY = scroller.currY
            drawMatrix.postTranslate(dx.toFloat(), dy.toFloat())
            fixBounds()
            notifyZoomChanged() // ← add here too
            postInvalidateOnAnimation()
        }
    }

    // ─────────────────────────────────────────────────────────────
    // TOUCH LOGIC (Pixel Perfect)
    // ─────────────────────────────────────────────────────────────

    private fun handleTap(x: Float, y: Float) {
        val pts = floatArrayOf(x, y)
        drawMatrix.invert(inverseMatrix)
        inverseMatrix.mapPoints(pts)
        val tx = pts[0]
        val ty = pts[1]

        // 1. First, check for a direct hit on the SELECTED color
        // This prioritizes the correct color even if there's overlap
        var targetGroup = groups.findLast { group ->
            !group.isStroke &&
                    group.tempColor == selectedColor && // Priority Check
                    !hidden.contains(group.id) &&
                    group.bounds.contains(tx, ty) &&
                    isPixelSolid(group, tx, ty)
        }

        // 2. If no direct hit on selected color, look for the nearest un-filled selected color
        if (targetGroup == null) {
            val searchRadius = 20f / currentScale // Adjust based on zoom level
            targetGroup = findGroupNear(tx, ty, searchRadius, onlySelected = true)
        }

        // 3. Final execution
        if (targetGroup != null) {
            // We found a valid mask to fill!
            // We pass tx/ty as the animation start point even if it's "near" the mask
            startFillAnimation(targetGroup, PointF(tx, ty))
        } else {
            // Only if we found absolutely nothing or only wrong colors within the radius
            showSingleToast("Please fill the selected color")
        }

        val isAllFilled = getColorFilled()

        if (isAllFilled) {
            colorProgressListener?.onComplete()
        }

    }

    private fun findGroupNear(tx: Float, ty: Float, radius: Float, onlySelected: Boolean): GroupItem? {
        var bestMatch: GroupItem? = null
        var minDistance = Float.MAX_VALUE

        val searchRect = RectF(tx - radius, ty - radius, tx + radius, ty + radius)

        for (group in groups) {
            if (group.isStroke || hidden.contains(group.id)) continue // Skip filled items!

            if (onlySelected && group.tempColor != selectedColor) continue

            if (RectF.intersects(group.bounds, searchRect)) {
                // If the touch is inside the bounds, distance is 0 (Highest Priority)
                val dist = if (group.bounds.contains(tx, ty)) 0f else distanceToRect(tx, ty, group.bounds)

                if (dist < radius && dist < minDistance) {
                    minDistance = dist
                    bestMatch = group
                }
            }
        }
        return bestMatch
    }

    private fun distanceToRect(x: Float, y: Float, rect: RectF): Float {
        val dx = max(0f, max(rect.left - x, x - rect.right))
        val dy = max(0f, max(rect.top - y, y - rect.bottom))
        return sqrt(dx * dx + dy * dy)
    }

    private fun isPixelSolid(group: GroupItem, tx: Float, ty: Float): Boolean {
        val localX = (tx - group.offsetX).toInt()
        val localY = (ty - group.offsetY).toInt()

        return try {
            val pixel = group.bitmap.getPixel(localX, localY)
            Color.alpha(pixel) > 10 // Consider "solid" if not transparent
        } catch (e: Exception) {
            false
        }
    }


    private fun startFillAnimation(group: GroupItem, point: PointF) {
        group.touchPoint = point
        hidden.add(group.id)

        // ✅ Start water drop ripple at tap point with group's color
        startRipple(point.x, point.y, group.tempColor)

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                group.animProgress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    colorProgressListener?.onProgressUpdated(getColorProgress())
                }
            })
            start()
        }
    }


    // Helper to calculate progress
    fun getColorProgress(): Map<Int, ColorProgress> {
        val totalMap = groups.filter { !it.isStroke }.groupBy { it.tempColor }
        return totalMap.mapValues { (_, colorGroups) ->
            val total = colorGroups.size
            val filled = colorGroups.count { hidden.contains(it.id) }
            ColorProgress(filled, total)
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UTILS & VIEWPORT
    // ─────────────────────────────────────────────────────────────

    private fun isWithinViewport(bounds: RectF): Boolean {
        val viewport = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val mappedBounds = RectF()
        drawMatrix.mapRect(mappedBounds, bounds)
        return RectF.intersects(viewport, mappedBounds)
    }

    private fun recomputeBgRects() {
        val bmp = bgBitmap ?: return
        if (contentWidth <= 0f) return
        val scale = max(contentWidth / bmp.width, contentHeight / bmp.height)
        val srcW = contentWidth / scale
        val srcH = contentHeight / scale
        val srcX = (bmp.width - srcW) / 2f
        val srcY = (bmp.height - srcH) / 2f
        bgSrcRect.set(srcX.toInt(), srcY.toInt(), (srcX + srcW).toInt(), (srcY + srcH).toInt())
        bgDestRect.set(0f, 0f, contentWidth, contentHeight)
    }

    private fun updateBase() {
        if (width == 0 || contentWidth <= 0f) return
        val scale = min(width / contentWidth, height / contentHeight)
        drawMatrix.setTranslate((width - contentWidth * scale) / 2f, (height - contentHeight * scale) / 2f)
        drawMatrix.preScale(scale, scale)
        baseScale = scale
        currentScale = scale
        baseMatrix.set(drawMatrix)   // ← save base state
        invalidate()
    }

    fun getCurrentDrawingBitmap(): Bitmap {
        // Use content dimensions, not bg bitmap dimensions
        val bitmapWidth = contentWidth.toInt().coerceAtLeast(1)
        val bitmapHeight = contentHeight.toInt().coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // ❌ Remove canvas.concat(drawMatrix) — that's for screen rendering only

        // 1. Draw Background in content space
        bgBitmap?.let { canvas.drawBitmap(it, bgSrcRect, bgDestRect, null) }

        // 2. Draw groups at their natural offsets (no matrix needed)
        for (group in groups) {
            val isFilled = hidden.contains(group.id)
            val isSelected = group.tempColor == selectedColor

            when {
                group.isStroke -> {
                    canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, null)
                }

                isFilled -> {
//                    canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, null)
                }

                isSelected -> {
                    paint.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, paint)
                    paint.colorFilter = null
                }

                else -> {
                    paint.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    canvas.drawBitmap(group.bitmap, group.offsetX, group.offsetY, paint)
                    paint.colorFilter = null
                }
            }
        }

        return bitmap
    }

    fun findAndZoomToNextPath(): Boolean {
        // 1. Find target
        val target = groups.find { !it.isStroke && !hidden.contains(it.id) && it.tempColor == selectedColor }

        if (target == null) {
            showSingleToast("Color completed!")
            return false
        }

        val viewW = width.toFloat()
        val viewH = height.toFloat()
        if (viewW <= 0 || viewH <= 0) return false

        // 2. DYNAMIC ZOOM CALCULATION
        val pathW = target.bounds.width()
        val pathH = target.bounds.height()

        // Calculate scale needed to fit the path into 60% of the screen
        val scaleToFitWidth = (viewW * 0.6f) / pathW
        val scaleToFitHeight = (viewH * 0.6f) / pathH

        // Choose the smaller scale to ensure the whole path fits
        var targetScale = min(scaleToFitWidth, scaleToFitHeight)

        // --- THE LIMIT LOGIC ---
        // baseScale: The scale where the image fits the screen initially.
        // maxScale: Your defined maximum (e.g., 300f).
        // We clamp targetScale so it never exceeds your maxScale.
        targetScale = targetScale.coerceIn(baseScale, maxScale)

        val centerX = target.bounds.centerX()
        val centerY = target.bounds.centerY()

        val startMatrix = Matrix(drawMatrix)
        val endMatrix = Matrix()

        // 3. Setup Target Matrix
        // Use reset() to ensure we are building the scale from 1.0f correctly
        endMatrix.setScale(targetScale, targetScale)

        // Calculate the translation to center the path's center point
        val transX = (viewW / 2f) - (centerX * targetScale)
        val transY = (viewH / 2f) - (centerY * targetScale)
        endMatrix.postTranslate(transX, transY)

        // 4. ANIMATION (Camera Move)
        val floatValues = FloatArray(9)
        val startValues = FloatArray(9)
        val endValues = FloatArray(9)
        startMatrix.getValues(startValues)
        endMatrix.getValues(endValues)

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val fraction = anim.animatedValue as Float
                for (i in 0..8) {
                    floatValues[i] = startValues[i] + (endValues[i] - startValues[i]) * fraction
                }
                drawMatrix.setValues(floatValues)

                // Keep currentScale synced with the matrix for the ScaleDetector
                currentScale = floatValues[Matrix.MSCALE_X]
                notifyZoomChanged()
                invalidate()
            }
            start()
        }

        // 5. Visual Cue (Blink) remains the same...
        startBlinkAnimation(target)
        return true
    }

    fun resetZoom() {
        val startMatrix = Matrix(drawMatrix)
        val endMatrix = Matrix()

        // Rebuild the original base matrix
        val scale = min(width / contentWidth, height / contentHeight)
        endMatrix.setTranslate((width - contentWidth * scale) / 2f, (height - contentHeight * scale) / 2f)
        endMatrix.preScale(scale, scale)

        val startValues = FloatArray(9)
        val endValues = FloatArray(9)
        val floatValues = FloatArray(9)
        startMatrix.getValues(startValues)
        endMatrix.getValues(endValues)

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val fraction = anim.animatedValue as Float
                for (i in 0..8) {
                    floatValues[i] = startValues[i] + (endValues[i] - startValues[i]) * fraction
                }
                drawMatrix.setValues(floatValues)
                currentScale = floatValues[Matrix.MSCALE_X]
                notifyZoomChanged()
                invalidate()
            }
            start()
        }
    }

    private fun startBlinkAnimation(target: GroupItem) {

        ValueAnimator.ofInt(0, 180, 0, 180, 0).apply {
            duration = 1200
            addUpdateListener { anim ->
                target.blinkAlpha = anim.animatedValue as Int
                invalidate()
            }
            start()
        }

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Important: Always pass to scale detector first
        scaleDetector.onTouchEvent(event)

        // Only let the gesture detector handle things if we aren't scaling
        // This stops the "view jumping to other finger" bug
        if (!scaleDetector.isInProgress) {
            gestureDetector.onTouchEvent(event)
        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            scroller.forceFinished(true)
        }
        return true
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val nextScale = currentScale * scaleFactor

            // Clamp between half the starting size and 300x zoom
            val targetScale = nextScale.coerceIn(baseScale * 0.5f, maxScale)

            val adjustedFactor = targetScale / currentScale
            currentScale = targetScale

            drawMatrix.postScale(adjustedFactor, adjustedFactor, detector.focusX, detector.focusY)
            fixBounds()
            notifyZoomChanged()
            invalidate()
            return true
        }
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, dX: Float, dY: Float): Boolean {
            drawMatrix.postTranslate(-dX, -dY)
            fixBounds()
            notifyZoomChanged()
            invalidate()
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, vX: Float, vY: Float): Boolean {
            lastFlingX = 0
            lastFlingY = 0
            scroller.fling(
                0, 0, vX.toInt(), vY.toInt(),
                Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE
            )
            postInvalidateOnAnimation()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            handleTap(e.x, e.y)
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val pts = floatArrayOf(e.x, e.y)
            drawMatrix.invert(inverseMatrix)
            inverseMatrix.mapPoints(pts)

            val tx = pts[0]
            val ty = pts[1]


            val searchRadius = 20f / currentScale // Adjust based on zoom level

            val tappedGroup = groups.findLast { group ->
                !group.isStroke &&
                        group.bounds.contains(tx, ty) &&
                        isPixelSolid(group, tx, ty)
            } ?: findGroupNear(tx, ty, searchRadius, onlySelected = false)


            if (tappedGroup != null && !tappedGroup.isStroke) {
                // Trigger the callback to MainActivity
                onColorLongClickListener?.onColorSelected(tappedGroup.tempColor)

                // Optional: Provide haptic feedback (vibration)
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }


        }
    }


    private var lastToastTime = 0L

    private fun showSingleToast(msg: String) {
        val now = System.currentTimeMillis()

        // Prevent spam (1 toast per 1 sec)
        if (now - lastToastTime > 1000) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            lastToastTime = now
        }
    }

    // Inside ColoringView class properties
    private val matrixRect = RectF()

    /**
     * Constrains the translation so the image doesn't disappear.
     * "at least an edge at screen center" logic.
     */
    private fun fixBounds() {
        val bmpW = contentWidth
        val bmpH = contentHeight
        if (bmpW <= 0f || bmpH <= 0f) return

        // 1. Get the current transformed coordinates of our content
        matrixRect.set(0f, 0f, bmpW, bmpH)
        drawMatrix.mapRect(matrixRect)

        val viewW = width.toFloat()
        val viewH = height.toFloat()

        var dx = 0f
        var dy = 0f

        // 2. Horizontal Constraints
        // If the right edge of the image moves further left than the screen center
        if (matrixRect.right < viewW / 2f) {
            dx = viewW / 2f - matrixRect.right
        }
        // If the left edge of the image moves further right than the screen center
        if (matrixRect.left > viewW / 2f) {
            dx = viewW / 2f - matrixRect.left
        }

        // 3. Vertical Constraints
        // If the bottom edge moves above the screen center
        if (matrixRect.bottom < viewH / 2f) {
            dy = viewH / 2f - matrixRect.bottom
        }
        // If the top edge moves below the screen center
        if (matrixRect.top > viewH / 2f) {
            dy = viewH / 2f - matrixRect.top
        }

        // 4. Apply the correction
        if (dx != 0f || dy != 0f) {
            drawMatrix.postTranslate(dx, dy)
        }
    }

    override fun onDetachedFromWindow() {
        // Cancel all ripple animations
        ripples.forEach { it.animator?.cancel() }
        ripples.clear()

        // Cancel any fill animations in groups
        // If you have ValueAnimators saved in GroupItems, cancel them here

        super.onDetachedFromWindow()
    }




}

interface OnColorProgressListener {
    fun onProgressUpdated(colorProgressMap: Map<Int, ColorProgress>)
    fun onComplete()
}

interface OnColorLongClickListener {
    fun onColorSelected(color: Int)
}

data class ColorProgress(val filledCount: Int, val totalCount: Int)
