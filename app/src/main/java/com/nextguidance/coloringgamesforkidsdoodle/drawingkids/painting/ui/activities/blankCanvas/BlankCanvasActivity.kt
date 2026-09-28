package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.blankCanvas

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.customWidgets.DrawingView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityBlankCanvasBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.ColorPaletteAdapter
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog.SaveShareDialogFragment
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.ColorPaletteProvider
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
class BlankCanvasActivity :
    BaseActivity<ActivityBlankCanvasBinding>(ActivityBlankCanvasBinding::inflate) {

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupCanvas()
        setupTopBar()
        setupTools()
        setupBrushSlider()
        setupColorPalette()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun setupCanvas() {
        binding.drawingView.setCanvasBackgroundColor(Color.WHITE)
        binding.drawingView.isNeonCoreEnabled = false
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Open Save & Share Dialog
        binding.btnSave.setOnClickListener {
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

    private fun setupTools() {
        // Default to Brush
        selectTool(DrawingView.ToolMode.BRUSH)

        binding.btnToolBrush.setOnClickListener {
            selectTool(DrawingView.ToolMode.BRUSH)
        }

        binding.btnToolEraser.setOnClickListener {
            selectTool(DrawingView.ToolMode.ERASER)
        }
    }

    private fun selectTool(mode: DrawingView.ToolMode) {
        binding.drawingView.currentTool = mode

        // Tactile scale & alpha visual feedback
        val tools = listOf(
            binding.btnToolBrush to (mode == DrawingView.ToolMode.BRUSH),
            binding.btnToolEraser to (mode == DrawingView.ToolMode.ERASER)
        )

        tools.forEach { (view, isSelected) ->
            view.alpha = if (isSelected) 1.0f else 0.45f
            view.scaleX = if (isSelected) 1.05f else 0.95f
            view.scaleY = if (isSelected) 1.05f else 0.95f
        }
    }

    private fun setupBrushSlider() {
        binding.sliderBrushSize.addOnChangeListener { _, value, _ ->
            binding.drawingView.currentStrokeWidth = value
        }
        binding.drawingView.currentStrokeWidth = binding.sliderBrushSize.value
    }

    private fun setupColorPalette() {
        val colorItems = ColorPaletteProvider.allColors

        binding.drawingView.currentColor = colorItems.first().colorInt

        val adapter = ColorPaletteAdapter(colorItems) { selected ->
            binding.drawingView.currentColor = selected.colorInt
            // Auto switch back to Brush if user picks a color while using Eraser
            if (binding.drawingView.currentTool == DrawingView.ToolMode.ERASER) {
                selectTool(DrawingView.ToolMode.BRUSH)
            }
        }
        binding.rvColors.adapter = adapter
    }

    private fun celebrateAndExit() {
        // Prevent multiple taps
        binding.btnFinish.isEnabled = false

        //  Cheer voice praise
        voiceAssistant.speakSuccess()

        //  Confetti party animation
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(
                Color.RED,
                Color.YELLOW,
                Color.GREEN,
                Color.MAGENTA,
                Color.CYAN
            ),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        binding.konfettiView.start(party)

        //  Return back after 3 seconds
        lifecycleScope.launch {
            delay(3000)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}