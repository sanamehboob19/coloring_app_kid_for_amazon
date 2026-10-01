package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.splash

import android.os.Bundle
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.lifecycle.lifecycleScope
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivitySplashBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.main.MainActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ShareUtil
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private var titlePulseAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MusicManager.pauseForCurrentScreen()
        setupVideoBackground()
        setupButtons()
        triggerDelayedEntranceAnimations()
    }

    override fun onResume() {
        super.onResume()
        binding.videoViewSplash.start()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            binding.videoViewSplash.setAudioFocusRequest(android.media.AudioManager.AUDIOFOCUS_NONE)
        }
        MusicManager.pauseForCurrentScreen()
    }

    override fun onPause() {
        super.onPause()
        binding.videoViewSplash.pause()
    }

    private fun setupVideoBackground() {
        val isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        val videoResId = if (isLandscape) R.raw.splash_horizontal else R.raw.splash_vertical
        val videoUri = Uri.parse("android.resource://$packageName/$videoResId")

        binding.videoViewSplash.apply {
            setVideoURI(videoUri)
            setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.setVolume(0f, 0f)
                post {
                    val videoWidth = mp.videoWidth.toFloat()
                    val videoHeight = mp.videoHeight.toFloat()
                    val viewWidth = width.toFloat()
                    val viewHeight = height.toFloat()

                    val videoRatio = videoWidth / videoHeight
                    val viewRatio = viewWidth / viewHeight

                    if (videoRatio > viewRatio) {
                        scaleX = videoRatio / viewRatio
                        scaleY = 1f
                    } else {
                        scaleX = 1f
                        scaleY = viewRatio / videoRatio
                    }
                    pivotX = viewWidth / 2f
                    pivotY = viewHeight / 2f
                    start()
                }
            }
        }
    }

    private fun triggerDelayedEntranceAnimations() {
        lifecycleScope.launch {
            delay(1000)
            binding.layoutTopButtons.animate().alpha(1.0f).setDuration(600).start()
            binding.appNameCard.animate().alpha(1.0f).setDuration(600).withEndAction {
                startTitlePulseAnimation()
            }.start()

            delay(250)
            binding.btnPlay.visibility = View.VISIBLE
            binding.btnPlay.scaleX = 0.2f
            binding.btnPlay.scaleY = 0.2f
            binding.btnPlay.alpha = 1.0f
            binding.btnPlay.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800)
                .setInterpolator(OvershootInterpolator(2.2f))
                .withEndAction {
                    // 📺 Automatically focus the play button for TV Remotes when it pops in
                    binding.btnPlay.requestFocus()
                }
                .start()
        }
    }

    private fun startTitlePulseAnimation() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.95f, 1.06f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.95f, 1.06f)
        titlePulseAnimator = ObjectAnimator.ofPropertyValuesHolder(binding.appNameCard, scaleX, scaleY).apply {
            duration = 900
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun setupButtons() {
        val playAction = {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnPlay.setOnClickListener {
            playAction()
        }

        // 🌟 Ensure TV Remote center click triggers the click listener reliably
        binding.btnPlay.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP &&
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                playAction()
                true
            } else {
                false
            }
        }

        binding.btnSettings.setOnClickListener {
            ToastUtils.show("Settings coming soon!")
        }
        binding.btnRate.setOnClickListener {
            ToastUtils.show("Thank you for rating us!")
        }
        binding.btnShare.setOnClickListener {
            ShareUtil.shareApp(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        titlePulseAnimator?.cancel()
    }
}