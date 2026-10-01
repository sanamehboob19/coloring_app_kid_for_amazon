package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.simpleColoring

import android.os.Bundle
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.customWidgets.DrawingView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.ColoringTemplate
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityColoringCanvasBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.ColorPaletteAdapter
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.SaveShareDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ColorPaletteProvider
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.VoiceAssistant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class ColoringCanvasActivity :
    BaseActivity<ActivityColoringCanvasBinding>
        (ActivityColoringCanvasBinding::inflate) {

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    private var selectedTemplate: ColoringTemplate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractIntentData()
        setupTopBar()
        setupToolButtons()
        setupColorPalette()
        loadTemplateIntoCanvas()
        setupBrushSlider()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun extractIntentData() {
        selectedTemplate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TemplateSelectionActivity.EXTRA_TEMPLATE, ColoringTemplate::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TemplateSelectionActivity.EXTRA_TEMPLATE)
        }
    }

    private fun loadTemplateIntoCanvas() {
        val imageUrl = selectedTemplate?.previewUrl ?: ""
        Log.d("CanvasDebug", "Loading template URL into canvas: $imageUrl")

        if (imageUrl.isBlank()) {
            // Fallback to default local drawable if URL is empty
            val templateRes = selectedTemplate?.previewRes ?: R.drawable.a1
            binding.drawingView.post {
                binding.drawingView.loadTemplate(templateRes)
            }
            return
        }

        // Use Glide to download the remote GitHub image as a Bitmap for the canvas
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    Log.d("CanvasDebug", "Successfully downloaded remote bitmap for canvas!")
                    binding.drawingView.post {
                        binding.drawingView.loadTemplateBitmap(resource)
                    }
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}

                override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Log.e("CanvasDebug", "Failed to load remote bitmap from URL: $imageUrl")
                    // Fallback on failure
                    binding.drawingView.post {
                        binding.drawingView.loadTemplate(R.drawable.a1)
                    }
                }
            })
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave?.setOnClickListener {
            val bitmap = binding.drawingView.getCanvasBitmap() ?: return@setOnClickListener
            SaveShareDialogFragment.show(supportFragmentManager, bitmap)
        }

        binding.btnUndo.setOnClickListener {
            binding.drawingView.undo()
        }

        binding.btnClear.setOnClickListener {
            binding.drawingView.resetCanvas()
        }

        binding.btnFinish.setOnClickListener {
            celebrateAndExit()
        }
    }

    private fun setupToolButtons() {
        selectTool(DrawingView.ToolMode.BRUSH)

        binding.btnToolBucket.setOnClickListener { selectTool(DrawingView.ToolMode.BUCKET) }
        binding.btnToolBrush.setOnClickListener { selectTool(DrawingView.ToolMode.BRUSH) }
        binding.btnToolGlow.setOnClickListener { selectTool(DrawingView.ToolMode.GLOW) }
        binding.btnToolEraser.setOnClickListener { selectTool(DrawingView.ToolMode.ERASER) }
    }

    private fun selectTool(mode: DrawingView.ToolMode) {
        binding.drawingView.currentTool = mode

        val tools = listOf(
            binding.btnToolBucket to (mode == DrawingView.ToolMode.BUCKET),
            binding.btnToolBrush to (mode == DrawingView.ToolMode.BRUSH),
            binding.btnToolGlow to (mode == DrawingView.ToolMode.GLOW),
            binding.btnToolEraser to (mode == DrawingView.ToolMode.ERASER)
        )

        binding.layoutSliderRow?.visibility = if (mode == DrawingView.ToolMode.BUCKET) View.INVISIBLE else View.VISIBLE

        tools.forEach { (view, isSelected) ->
            view.alpha = if (isSelected) 1.0f else 0.45f
            view.scaleX = if (isSelected) 1.05f else 0.95f
            view.scaleY = if (isSelected) 1.05f else 0.95f
        }
    }

    private fun setupBrushSlider() {
        binding.sliderBrushSize?.addOnChangeListener { _, value, _ ->
            binding.drawingView.currentStrokeWidth = value
        }
        binding.drawingView.currentStrokeWidth = binding.sliderBrushSize?.value ?: 25f
    }

    private fun setupColorPalette() {
        val colorItems = ColorPaletteProvider.allColors
        binding.drawingView.currentColor = colorItems.first().colorInt

        val adapter = ColorPaletteAdapter(colorItems) { selected ->
            binding.drawingView.currentColor = selected.colorInt
            if (binding.drawingView.currentTool == DrawingView.ToolMode.ERASER) {
                selectTool(DrawingView.ToolMode.BUCKET)
            }
        }
        binding.rvColors.adapter = adapter
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

    override fun onDestroy() {
        super.onDestroy()
    }
}