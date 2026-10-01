package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.widget.CircularCountdownBulbView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Controls the idle bulb animation lifecycle.
 * Now accepts a [CircularCountdownBulbView] so it can drive the countdown arc.
 *
 * Sequence per cycle:
 *  1. Fade in at center  → countdown arc starts (total = centerMs + cornerMs)
 *  2. Wait [centerDurationMs]
 *  3. Float to top-left corner
 *  4. Wait [cornerDurationMs]    ← arc reaches 0 here
 *  5. Fade out
 *  6. [onCycleComplete] → caller re-arms idle timer
 */
class IdleBulbController(
    private val bulbView: CircularCountdownBulbView,   // ← type changed
    private val rootView: ViewGroup,
    private val centerDurationMs: Long = 3_000L,
    private val cornerDurationMs: Long = 3_000L,
    private val animationDurationMs: Long = 500L,
    private val cornerMarginPx: Int = 48,
    private val onBulbClicked: () -> Unit,
    private val onCycleComplete: () -> Unit
)
{
    private var animationJob: Job? = null
    private var scope: CoroutineScope? = null

    // Total visible time = center + corner durations
    private val totalCountdownMs get() = centerDurationMs + cornerDurationMs

    fun start(coroutineScope: CoroutineScope) {
        if (animationJob?.isActive == true) return
        scope = coroutineScope
        bulbView.setOnClickListener { onBulbClicked() }
        launchCycle()
    }

    fun stop() {
        animationJob?.cancel()
        animationJob = null
        bulbView.animate().cancel()
        bulbView.stopCountdown()
        bulbView.translationX = 0f
        bulbView.translationY = 0f
        bulbView.alpha = 0f
        bulbView.visibility = View.INVISIBLE
    }

    private fun launchCycle() {
        animationJob?.cancel()
        animationJob = scope?.launch {
            showAtCenter()           // fade in + start countdown arc
            delay(centerDurationMs)
            if (!isActive) return@launch

            floatToCorner()          // arc still draining during float
            delay(cornerDurationMs)  // arc reaches ~0 here
            if (!isActive) return@launch

            hide()

            if (isActive) onCycleComplete()
        }
    }

    private suspend fun showAtCenter() = withContext(Dispatchers.Main) {
        bulbView.translationX = 0f
        bulbView.translationY = 0f
        bulbView.alpha = 0f
        bulbView.visibility = View.VISIBLE

        // Start countdown arc for full visible duration
        bulbView.startCountdown(totalCountdownMs)

        suspendCancellableCoroutine { cont ->
            bulbView.animate()
                .alpha(1f)
                .setDuration(animationDurationMs)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction { if (cont.isActive) cont.resumeWith(Result.success(Unit)) }
                .start()
            cont.invokeOnCancellation { bulbView.animate().cancel() }
        }
    }

    private suspend fun floatToCorner() = withContext(Dispatchers.Main) {
        val targetX = cornerMarginPx.toFloat() - bulbView.left.toFloat()
        val targetY = cornerMarginPx.toFloat() - bulbView.top.toFloat()

        val animX = ObjectAnimator.ofFloat(bulbView, View.TRANSLATION_X, 0f, targetX)
        val animY = ObjectAnimator.ofFloat(bulbView, View.TRANSLATION_Y, 0f, targetY)

        suspendCancellableCoroutine { cont ->
            AnimatorSet().apply {
                playTogether(animX, animY)
                duration = animationDurationMs * 2
                interpolator = AccelerateDecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (cont.isActive) cont.resumeWith(Result.success(Unit))
                    }
                    override fun onAnimationCancel(animation: Animator) {
                        if (cont.isActive) cont.cancel()
                    }
                })
                start()
            }
            cont.invokeOnCancellation { animX.cancel(); animY.cancel() }
        }
    }

    private suspend fun hide() = withContext(Dispatchers.Main) {
        bulbView.stopCountdown()
        suspendCancellableCoroutine { cont ->
            bulbView.animate()
                .alpha(0f)
                .setDuration(animationDurationMs)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    bulbView.visibility = View.INVISIBLE
                    bulbView.translationX = 0f
                    bulbView.translationY = 0f
                    if (cont.isActive) cont.resumeWith(Result.success(Unit))
                }
                .start()
            cont.invokeOnCancellation { bulbView.animate().cancel() }
        }
    }
}