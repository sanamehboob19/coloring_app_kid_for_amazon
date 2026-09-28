package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Sequence:
 *  Phase 1 — Number pops in (overshoot scale) on the root overlay
 *  Phase 2 — itemView sets GONE + width collapses (neighbours slide in)
 *  Phase 3 — Floating label travels upward ~120dp
 *  Phase 4 — Particle burst + label fades out simultaneously
 */
object NumberPopAnimator {

    private const val FLOAT_DISTANCE_DP = 120f
    private const val POP_DURATION_MS   = 220L
    private const val COLLAPSE_DURATION = 320L
    private const val FLOAT_DURATION_MS = 380L
    private const val BURST_DURATION_MS = 500L
    private const val PARTICLE_COUNT    = 10

    fun play(
        itemView: View,
        numberView: View,
        color: Int,
        label: String,
        onRemove: () -> Unit
    ) {
        val rootView = itemView.rootView as? ViewGroup ?: return

        // ── Capture window-level coordinates before anything changes ──────────
        val srcLoc  = IntArray(2).also { numberView.getLocationInWindow(it) }
        val rootLoc = IntArray(2).also { rootView.getLocationInWindow(it) }

        val startX = srcLoc[0] - rootLoc[0] + numberView.width  / 2f
        val startY = srcLoc[1] - rootLoc[1] + numberView.height / 2f

        val floatDistPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, FLOAT_DISTANCE_DP, rootView.resources.displayMetrics
        )
        val endY = startY - floatDistPx

        // ── Build floating label (added to root so it escapes RV clipping) ────
        val labelSize = numberView.width
            .coerceAtLeast(numberView.height)
            .coerceAtLeast(dpToPx(rootView, 32f).toInt())

        val floatingLabel = TextView(rootView.context).apply {
            text     = label
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            typeface = Typeface.DEFAULT_BOLD
            background = buildCircleDrawable(color)
            gravity  = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(labelSize, labelSize)
            elevation    = 24f
            alpha  = 0f
            scaleX = 0f
            scaleY = 0f
            translationX = startX - labelSize / 2f
            translationY = startY - labelSize / 2f
        }
        rootView.addView(floatingLabel)

        // ── Phase 1: Pop in ───────────────────────────────────────────────────
        val popIn = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(floatingLabel, View.ALPHA,   0f, 1f),
                ObjectAnimator.ofFloat(floatingLabel, View.SCALE_X, 0f, 1.3f),
                ObjectAnimator.ofFloat(floatingLabel, View.SCALE_Y, 0f, 1.3f)
            )
            duration     = POP_DURATION_MS
            interpolator = OvershootInterpolator(3f)
        }

        // ── Phase 2: itemView GONE + width collapse ───────────────────────────
        // Run collapse AFTER popIn ends. The floating label stays visible above
        // while the slot in the RV closes and neighbours slide in.
        val collapseAnim = buildCollapseAnimator(itemView) {
            // itemView is now width=0; set GONE so layout no longer reserves space
            itemView.visibility = View.GONE
            onRemove()
        }

        // ── Phase 3: Float upward ─────────────────────────────────────────────
        val floatUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    floatingLabel, View.TRANSLATION_Y,
                    floatingLabel.translationY,
                    floatingLabel.translationY - floatDistPx
                ),
                ObjectAnimator.ofFloat(floatingLabel, View.SCALE_X, 1.3f, 1f),
                ObjectAnimator.ofFloat(floatingLabel, View.SCALE_Y, 1.3f, 1f)
            )
            duration     = FLOAT_DURATION_MS
            interpolator = DecelerateInterpolator()
        }

        // ── Phase 4: Burst + label fade simultaneously ────────────────────────
        val fadeOut = ObjectAnimator.ofFloat(floatingLabel, View.ALPHA, 1f, 0f).apply {
            duration = 180L
        }

        val burstView = ParticleBurstView(
            context       = rootView.context,
            cx            = startX,
            cy            = endY,
            color         = color,
            particleCount = PARTICLE_COUNT
        ).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            elevation = 23f
        }
        rootView.addView(burstView)

        val endPhase = AnimatorSet().apply {
            playTogether(fadeOut, burstView.buildAnimator(BURST_DURATION_MS))
        }

        // ── Chain: popIn → collapse → floatUp → burst/fade ───────────────────
        popIn.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Phase 2 starts right after pop completes
                collapseAnim.start()

                // Phase 3 runs in parallel with collapse so the label is already
                // moving while the slot closes — feels snappier
                floatUp.start()
            }
        })

        floatUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                endPhase.start()
            }
        })

        endPhase.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                rootView.removeView(floatingLabel)
                rootView.removeView(burstView)
            }
        })

        popIn.start()
    }

    private fun buildCollapseAnimator(itemView: View, onEnd: () -> Unit): Animator {
        val startWidth  = itemView.width
        val startMargin = (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd ?: 0

        return ValueAnimator.ofFloat(1f, 0f).apply {
            duration     = COLLAPSE_DURATION
            interpolator = AccelerateInterpolator()
            addUpdateListener { va ->
                val f  = va.animatedValue as Float
                val lp = itemView.layoutParams as? ViewGroup.MarginLayoutParams ?: return@addUpdateListener
                lp.width     = (startWidth  * f).toInt()
                lp.marginEnd = (startMargin * f).toInt()
                itemView.layoutParams = lp
                itemView.alpha = f.coerceIn(0f, 1f)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd()
                }
            })
        }
    }

    private fun buildCircleDrawable(color: Int): Drawable =
        ShapeDrawable(
            OvalShape()
        ).apply { paint.color = color }

    private fun dpToPx(view: View, dp: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, view.resources.displayMetrics)
}

// ─────────────────────────────────────────────────────────────────────────────
// ParticleBurstView — unchanged
// ─────────────────────────────────────────────────────────────────────────────

private class ParticleBurstView(
    context: Context,
    private val cx: Float,
    private val cy: Float,
    private val color: Int,
    private val particleCount: Int
) : View(context)
{

    data class Particle(
        val angle: Float,
        val speed: Float,
        val radius: Float,
        var x: Float = 0f,
        var y: Float = 0f,
        var currentRadius: Float = 0f,
        var alpha: Int = 255
    )

    private val particles: List<Particle>
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress = 0f
    private val colors: List<Int>

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        val rng = Random(System.currentTimeMillis())
        val baseRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics
        )
        val maxSpeed = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 80f, resources.displayMetrics
        )
        particles = List(particleCount) { i ->
            val angle = (2 * Math.PI / particleCount * i + rng.nextDouble() * 0.4).toFloat()
            Particle(
                angle  = angle,
                speed  = maxSpeed * (0.6f + rng.nextFloat() * 0.4f),
                radius = baseRadius * (0.7f + rng.nextFloat() * 0.6f)
            )
        }
        colors = listOf(
            color,
            blendColors(color, Color.WHITE, 0.4f),
            blendColors(color, Color.WHITE, 0.7f),
            blendColors(color, Color.BLACK, 0.2f)
        )
    }

    fun buildAnimator(durationMs: Long): Animator =
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration     = durationMs
            interpolator = DecelerateInterpolator()
            addUpdateListener { va ->
                progress = va.animatedValue as Float
                invalidate()
            }
        }

    override fun onDraw(canvas: Canvas) {
        val eased = 1f - (1f - progress) * (1f - progress)
        particles.forEachIndexed { i, p ->
            p.x = cx + cos(p.angle) * p.speed * eased
            p.y = cy + sin(p.angle) * p.speed * eased + (50f * progress * progress)
            p.currentRadius = p.radius * (1f - progress * 0.5f)
            p.alpha = ((1f - progress) * 255).toInt().coerceIn(0, 255)
            paint.color = colors[i % colors.size]
            paint.alpha = p.alpha
            canvas.drawCircle(p.x, p.y, p.currentRadius, paint)
        }
    }

    private fun blendColors(c1: Int, c2: Int, ratio: Float): Int {
        val inv = 1f - ratio
        return Color.rgb(
            (Color.red(c1)   * inv + Color.red(c2)   * ratio).toInt(),
            (Color.green(c1) * inv + Color.green(c2) * ratio).toInt(),
            (Color.blue(c1)  * inv + Color.blue(c2)  * ratio).toInt()
        )
    }
}