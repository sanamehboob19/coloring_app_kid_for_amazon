package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.toColorInt

/**
 * An ImageView that draws a circular countdown arc around itself.
 *
 * Usage:
 *   call startCountdown(totalMs) when the bulb appears.
 *   call stopCountdown()         when the bulb is dismissed early.
 *
 * The arc starts full (360°) and drains to 0 over [totalMs] milliseconds.
 * Color and stroke width are configurable.
 */
class CircularCountdownBulbView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr)
{

    // ── Arc appearance ────────────────────────────────────────────────────────
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style  = Paint.Style.STROKE
        color  = "#33FFFFFF".toColorInt()   // faint white track
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style  = Paint.Style.STROKE
        color  = "#FFA500".toColorInt()     // amber countdown arc
        strokeCap = Paint.Cap.ROUND
    }

    private val arcRect = RectF()
    private var strokeWidth = 0f

    // ── State ─────────────────────────────────────────────────────────────────
    private var sweepAngle   = 360f   // current arc sweep (360 = full, 0 = empty)
    private var totalMs      = 6_000L
    private var startTimeMs  = 0L
    private var isRunning    = false

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Start the countdown arc.
     * @param durationMs total visible duration (centerDurationMs + cornerDurationMs)
     */
    fun startCountdown(durationMs: Long) {
        totalMs     = durationMs
        startTimeMs = System.currentTimeMillis()
        sweepAngle  = 360f
        isRunning   = true
        invalidate()
    }

    fun stopCountdown() {
        isRunning  = false
        sweepAngle = 0f
        invalidate()
    }

    // ── Drawing ───────────────────────────────────────────────────────────────

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Stroke = ~10% of the smaller dimension
        strokeWidth = minOf(w, h) * 0.09f
        trackPaint.strokeWidth    = strokeWidth
        progressPaint.strokeWidth = strokeWidth

        val inset = strokeWidth / 2f
        arcRect.set(inset, inset, w - inset, h - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)   // draws the bulb image first

        if (!isRunning && sweepAngle == 0f) return

        // Recompute sweep from real elapsed time (no Animator needed — self-driven)
        if (isRunning) {
            val elapsed  = System.currentTimeMillis() - startTimeMs
            val fraction = (elapsed.toFloat() / totalMs).coerceIn(0f, 1f)
            sweepAngle   = 360f * (1f - fraction)

            if (sweepAngle <= 0f) {
                isRunning  = false
                sweepAngle = 0f
            } else {
                // Schedule next frame
                postInvalidateOnAnimation()
            }
        }

        // Track (background ring)
        canvas.drawArc(arcRect, -90f, 360f, false, trackPaint)

        // Progress arc (drains clockwise)
        if (sweepAngle > 0f) {
            canvas.drawArc(arcRect, -90f, sweepAngle, false, progressPaint)
        }
    }
}