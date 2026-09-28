package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.customWidgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import androidx.core.graphics.PathParser
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.NumberModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders.NumberPathsData
import java.util.Random
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class NumberTracingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
{

    var onCompleted: (() -> Unit)? = null

    private var currentNumber: NumberModel? = null

    // Multi-Stroke Engine
    private data class StrokeInfo(
        val path: Path,
        val measure: PathMeasure,
        val length: Float,
        val guideDots: List<PointF>
    )

    private val allStrokes = mutableListOf<StrokeInfo>()
    private val completedStrokesPath = Path()
    private val activeTracedSegment = Path()

    private var currentStrokeIndex = 0
    private var currentTracedLength = 0f

    private val avatarPos = floatArrayOf(0f, 0f)
    private var isCompleted = false
    private var isDragging = false

    // Paints
    private val tubeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = Color.parseColor("#37474F")
    }

    private val tubeWhiteCorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        color = Color.WHITE
    }

    private val guideDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#00E676")
    }

    private val glitterFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val avatarBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFD600")
    }

    private val avatarBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.parseColor("#FF6D00")
    }

    private val sparklePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // Sparkles
    private data class Sparkle(var x: Float, var y: Float, val vx: Float, val vy: Float, var alpha: Int, val size: Float, val color: Int)
    private val sparkles = mutableListOf<Sparkle>()
    private val random = Random()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun loadNumber(model: NumberModel) {
        currentNumber = model
        isCompleted = false
        isDragging = false
        currentStrokeIndex = 0
        currentTracedLength = 0f
        completedStrokesPath.reset()
        activeTracedSegment.reset()
        sparkles.clear()

        val color = Color.parseColor(model.glitterColorHex)
        glitterFillPaint.color = color

        if (width > 0 && height > 0) {
            buildStrokes()
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && currentNumber != null) {
            buildStrokes()
        }
    }

    private fun buildStrokes() {
        val num = currentNumber?.number ?: return
        allStrokes.clear()
        completedStrokesPath.reset()
        activeTracedSegment.reset()

        val padX = width * 0.12f
        val padY = height * 0.12f
        val targetRect = RectF(padX, padY, width - padX, height - padY)
        val srcRect = RectF(0f, 0f, 100f, 100f)

        val matrix = Matrix()
        matrix.setRectToRect(srcRect, targetRect, Matrix.ScaleToFit.CENTER)

        val tubeWidth = (targetRect.width() * 0.16f).coerceIn(46f, 96f)
        tubeBorderPaint.strokeWidth = tubeWidth + 10f
        tubeWhiteCorePaint.strokeWidth = tubeWidth
        glitterFillPaint.strokeWidth = tubeWidth - 6f

        val strokeStrings = NumberPathsData.getStrokes(num)

        for (strokeSvg in strokeStrings) {
            val rawPath = PathParser.createPathFromPathData(strokeSvg)
            val scaledPath = Path()
            rawPath.transform(matrix, scaledPath)

            val measure = PathMeasure(scaledPath, false)
            val len = measure.length
            val dots = mutableListOf<PointF>()

            if (len > 0) {
                val dotSpacing = 30f
                val count = (len / dotSpacing).toInt()
                val pos = floatArrayOf(0f, 0f)

                for (i in 0..count) {
                    measure.getPosTan(i * dotSpacing, pos, null)
                    dots.add(PointF(pos[0], pos[1]))
                }
            }

            allStrokes.add(StrokeInfo(scaledPath, measure, len, dots))
        }

        // Position avatar at the start of Stroke 1
        if (allStrokes.isNotEmpty()) {
            allStrokes[0].measure.getPosTan(0f, avatarPos, null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (allStrokes.isEmpty()) return

        // 1. Draw Hollow Tubes for ALL strokes
        for (stroke in allStrokes) {
            canvas.drawPath(stroke.path, tubeBorderPaint)
            canvas.drawPath(stroke.path, tubeWhiteCorePaint)
        }

        // 2. Draw Guidance Dots for all strokes
        val dotRadius = width * 0.012f
        for (stroke in allStrokes) {
            for (dot in stroke.guideDots) {
                canvas.drawCircle(dot.x, dot.y, dotRadius, guideDotPaint)
            }
        }

        // 3. Draw Completed Strokes
        if (!completedStrokesPath.isEmpty) {
            canvas.drawPath(completedStrokesPath, glitterFillPaint)
        }

        // 4. Draw Active Tracing Segment of Current Stroke
        if (!activeTracedSegment.isEmpty) {
            canvas.drawPath(activeTracedSegment, glitterFillPaint)
        }

        // 5. Render Sparkles
        renderSparkles(canvas)

        // 6. Draw Avatar Star on the current active stroke
        if (!isCompleted) {
            drawAvatarStar(canvas, avatarPos[0], avatarPos[1], width * 0.038f)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isCompleted || allStrokes.isEmpty() || currentStrokeIndex >= allStrokes.size) return false

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val dist = hypot((x - avatarPos[0]).toDouble(), (y - avatarPos[1]).toDouble()).toFloat()
                if (dist < width * 0.15f) {
                    isDragging = true
                    spawnSparkles(avatarPos[0], avatarPos[1], 4)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    advanceActiveStroke(x, y)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
            }
        }
        invalidate()
        return true
    }

    private fun advanceActiveStroke(touchX: Float, touchY: Float) {
        val stroke = allStrokes[currentStrokeIndex]
        val searchAheadMax = 44f
        val step = 6f
        var bestDist = Float.MAX_VALUE
        var bestLength = currentTracedLength
        val testPos = floatArrayOf(0f, 0f)

        var l = currentTracedLength
        val limit = (currentTracedLength + searchAheadMax).coerceAtMost(stroke.length)

        while (l <= limit) {
            stroke.measure.getPosTan(l, testPos, null)
            val d = hypot((touchX - testPos[0]).toDouble(), (touchY - testPos[1]).toDouble()).toFloat()
            if (d < bestDist) {
                bestDist = d
                bestLength = l
            }
            l += step
        }

        if (bestDist < width * 0.14f && bestLength > currentTracedLength) {
            currentTracedLength = bestLength
            stroke.measure.getPosTan(currentTracedLength, avatarPos, null)

            activeTracedSegment.reset()
            stroke.measure.getSegment(0f, currentTracedLength, activeTracedSegment, true)

            spawnSparkles(avatarPos[0], avatarPos[1], 3)

            // Check if current stroke finished (96%)
            if (currentTracedLength >= stroke.length * 0.96f) {
                // Commit this stroke to completed path
                completedStrokesPath.addPath(stroke.path)
                activeTracedSegment.reset()
                currentTracedLength = 0f

                // Move to next stroke (e.g. from first '1' to second '1')
                if (currentStrokeIndex < allStrokes.size - 1) {
                    currentStrokeIndex++
                    allStrokes[currentStrokeIndex].measure.getPosTan(0f, avatarPos, null)
                    isDragging = false // Kid touches the new avatar position to start digit 2
                } else {
                    // All digits completed!
                    isCompleted = true
                    isDragging = false
                    onCompleted?.invoke()
                }
            }
        }
    }

    private fun drawAvatarStar(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        canvas.drawCircle(cx, cy, radius * 1.25f, avatarBodyPaint)
        canvas.drawCircle(cx, cy, radius * 1.25f, avatarBorderPaint)

        val starPath = Path()
        val spikes = 5
        val innerR = radius * 0.5f
        var rot = Math.PI / 2 * 3
        val step = Math.PI / spikes

        starPath.moveTo(cx, cy - radius)
        for (i in 0 until spikes) {
            starPath.lineTo(cx + cos(rot).toFloat() * radius, cy + sin(rot).toFloat() * radius)
            rot += step
            starPath.lineTo(cx + cos(rot).toFloat() * innerR, cy + sin(rot).toFloat() * innerR)
            rot += step
        }
        starPath.close()

        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#E65100")
        }
        canvas.drawPath(starPath, starPaint)
    }

    private fun spawnSparkles(x: Float, y: Float, count: Int) {
        val color = Color.parseColor(currentNumber?.glitterColorHex ?: "#FFD700")
        for (i in 0 until count) {
            val angle = random.nextDouble() * 2 * Math.PI
            val speed = 2f + random.nextFloat() * 4f
            sparkles.add(
                Sparkle(
                    x, y,
                    (cos(angle) * speed).toFloat(),
                    (sin(angle) * speed).toFloat(),
                    255,
                    5f + random.nextFloat() * 8f,
                    if (random.nextBoolean()) Color.WHITE else color
                )
            )
        }
    }

    private fun renderSparkles(canvas: Canvas) {
        if (sparkles.isEmpty()) return
        val iter = sparkles.iterator()
        while (iter.hasNext()) {
            val s = iter.next()
            s.x += s.vx
            s.y += s.vy
            s.alpha -= 14
            if (s.alpha <= 0) {
                iter.remove()
            } else {
                sparklePaint.color = s.color
                sparklePaint.alpha = s.alpha
                canvas.drawCircle(s.x, s.y, s.size / 2f, sparklePaint)
            }
        }
        postInvalidateOnAnimation()
    }
}
