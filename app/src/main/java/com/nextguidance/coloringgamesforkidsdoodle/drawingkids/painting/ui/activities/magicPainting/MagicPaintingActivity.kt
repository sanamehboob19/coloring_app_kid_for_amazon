package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.magicPainting

import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.MagicTemplateEntity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityMagicPaintingBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog.ExcellentBannerDialogFragment
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog.SaveShareDialogFragment
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.VoiceAssistant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MagicPaintingActivity : BaseActivity<ActivityMagicPaintingBinding>(ActivityMagicPaintingBinding::inflate) {

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    private var selectedTemplate: MagicTemplateEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractIntentData()
        setupTopBar()
        loadMagicImageFromUrl()
        setupAutoCompletion()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun extractIntentData() {
        selectedTemplate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MagicSelectionActivity.EXTRA_MAGIC_ENTITY, MagicTemplateEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(MagicSelectionActivity.EXTRA_MAGIC_ENTITY)
        }
    }

    private fun loadMagicImageFromUrl() {
        val imageUrl = selectedTemplate?.previewUrl ?: ""

        if (imageUrl.isBlank()) {
            binding.magicPaintingView.post {
                binding.magicPaintingView.loadColoredImage(R.drawable.a1)
            }
            return
        }

        // Download remote GitHub image URL via Glide and pass the bitmap to MagicPaintingView
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.magicPaintingView.post {
                        binding.magicPaintingView.loadColoredBitmap(resource)
                    }
                }
                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
                override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    binding.magicPaintingView.post {
                        binding.magicPaintingView.loadColoredImage(R.drawable.a1)
                    }
                }
            })
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnClear.setOnClickListener {
            binding.magicPaintingView.resetCanvas()
        }

        binding.btnSave.setOnClickListener {
            val bitmap = captureMagicViewBitmap()
            SaveShareDialogFragment.show(supportFragmentManager, bitmap)
        }

        binding.btnFinish.setOnClickListener {
            celebrateAndExit()
        }
    }

    private fun captureMagicViewBitmap(): Bitmap {
        val view = binding.magicPaintingView
        val bitmap = Bitmap.createBitmap(view.width.coerceAtLeast(100), view.height.coerceAtLeast(100), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    private fun celebrateAndExit() {
        binding.btnFinish.isEnabled = false
        voiceAssistant.speakSuccess()

        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(Color.RED, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        binding.konfettiView.start(party)

        lifecycleScope.launch {
            delay(3000)
            finish()
        }
    }

    private fun setupAutoCompletion() {
        binding.magicPaintingView.onPaintingCompleted = {
            ExcellentBannerDialogFragment.show(supportFragmentManager) {
                val bitmap = captureMagicViewBitmap()
                SaveShareDialogFragment.show(supportFragmentManager, bitmap)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}