package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.extension

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import android.view.View
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit


fun showLog(tag: String, message: String) {
    Log.d(tag, message)
}




class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeClick: (View) -> Unit
) : View.OnClickListener
{
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeClick(v)
    }
}


//  The Superpower  use this   (simple without animation )
fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}


/**
 * Replaces setOnClickListener with a scale-down/scale-up animation for immediate feedback.
 *
 * @param onClick The code to run when the press is confirmed (after the animation finishes).
 */
fun View.setOnAnimateClickListener(onClick: View.OnClickListener) {

    val scaleDownX = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.95f)
    val scaleDownY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.95f)
    val scaleUpX = ObjectAnimator.ofFloat(this, View.SCALE_X, 1.0f)
    val scaleUpY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1.0f)

    val pressSet = AnimatorSet().apply {
        playTogether(scaleDownX, scaleDownY)
        duration = 50
    }

    val releaseSet = AnimatorSet().apply {
        playTogether(scaleUpX, scaleUpY)
        duration = 50
    }

//    this.setOnClickListener { view ->
    this.setSafeOnClickListener { view ->
        pressSet.start()

        releaseSet.startDelay = pressSet.duration
        releaseSet.start()

        this.postDelayed({
            onClick.onClick(view)
        }, 100)
    }
}



fun KonfettiView.showConfetti(
    colors: List<Int> = listOf(
        Color.YELLOW,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN
    ),
    duration: Long = 150L,
    maxParticles: Int = 80
) {
    start(
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = colors,
            emitter = Emitter(
                duration = duration,
                TimeUnit.MILLISECONDS
            ).max(maxParticles)
        )
    )
}



