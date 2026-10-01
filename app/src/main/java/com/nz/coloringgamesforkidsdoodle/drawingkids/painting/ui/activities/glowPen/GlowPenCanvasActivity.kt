package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.glowPen

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.customWidgets.DrawingView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityGlowPenCanvasBinding
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
class GlowPenCanvasActivity :
    BaseActivity<ActivityGlowPenCanvasBinding>(ActivityGlowPenCanvasBinding::inflate)
{

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupGlowCanvas()
        setupTools()
        setupBrushSlider()
        setupBottomActionButtons()
        setupNeonColorPalette()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun setupGlowCanvas() {
        // Pitch-black canvas for high-contrast neon lighting
        binding.drawingView.setCanvasBackgroundColor(Color.BLACK)
        // Enables intense neon bloom with white laser core
        binding.drawingView.isNeonCoreEnabled = true
        selectTool(DrawingView.ToolMode.GLOW)
        binding.drawingView.currentStrokeWidth = binding.sliderBrushSize.value
    }

    private fun setupTools() {
        binding.btnToolGlow.setOnClickListener {
            selectTool(DrawingView.ToolMode.GLOW)
        }

        binding.btnToolEraser.setOnClickListener {
            selectTool(DrawingView.ToolMode.ERASER)
        }
    }

    private fun selectTool(mode: DrawingView.ToolMode) {
        binding.drawingView.currentTool = mode

        // 3D tactile scale and alpha visual feedback
        val tools = listOf(
            binding.btnToolGlow to (mode == DrawingView.ToolMode.GLOW),
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
    }

    private fun setupBottomActionButtons() {
        binding.btnClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

    private fun setupNeonColorPalette() {
        val colors = ColorPaletteProvider.allColors

        binding.drawingView.currentColor = colors.first().colorInt

        val adapter = ColorPaletteAdapter(colors) { selected ->
            binding.drawingView.currentColor = selected.colorInt
            // Auto switch back to Glow if user selects a color while erasing
            if (binding.drawingView.currentTool == DrawingView.ToolMode.ERASER) {
                selectTool(DrawingView.ToolMode.GLOW)
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
