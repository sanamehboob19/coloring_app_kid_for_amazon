package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.pixelArt

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.PixelArtModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityPixelArtBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders.PixelArtProvider
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.ExcellentBannerDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.VoiceAssistant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PixelArtActivity :
    BaseActivity<ActivityPixelArtBinding>(ActivityPixelArtBinding::inflate) {

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    private var currentIndex = 0
    private val modelsList = PixelArtProvider.models
    private val colorSwatches = mutableListOf<CardView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractSelectedModel()
        setupTopBar()
        setupNavigation()
        setupCompletionListener()
        loadCurrentModel()
    }

    private fun extractSelectedModel() {
        val selected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                PixelSelectionActivity.EXTRA_SELECTED_MODEL,
                PixelArtModel::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(PixelSelectionActivity.EXTRA_SELECTED_MODEL)
        }

        currentIndex = if (selected != null) {
            modelsList.indexOfFirst { it.id == selected.id }.coerceAtLeast(0)
        } else {
            0
        }
    }


    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun loadCurrentModel() {
        val model = modelsList[currentIndex]

        //  Update Title
        binding.tvModelTitle.text = model.title

        //  Load Target Reference (Left/Top)
        binding.targetGridView.loadModel(model, isTargetPreview = true)

        //  Load Interactive Drawing Board (Right/Center)
        binding.interactiveGridView.loadModel(model, isTargetPreview = false)

        //  Update Navigation Arrows
        binding.btnPrevModel.isEnabled = currentIndex > 0
        binding.btnPrevModel.alpha = if (currentIndex > 0) 1.0f else 0.4f

        binding.btnNextModel.isEnabled = currentIndex < modelsList.size - 1
        binding.btnNextModel.alpha = if (currentIndex < modelsList.size - 1) 1.0f else 0.4f

        //  Build Dynamic Palette
        buildColorPalette(model.paletteColorsHex)
    }

    private fun buildColorPalette(paletteHexList: List<String>) {
        binding.layoutColorPalette.removeAllViews()
        colorSwatches.clear()

        val size = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._32sdp)
        val margin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)

        paletteHexList.forEachIndexed { index, hex ->
            val color = Color.parseColor(hex)

            val card = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(margin, 0, margin, 0)
                }
                radius = size / 2f
                cardElevation = 4f
                setCardBackgroundColor(color)
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    selectPaletteColor(this, color)
                }
            }

            colorSwatches.add(card)
            binding.layoutColorPalette.addView(card)

            // Select first non-white color by default
            if (index == 1) {
                selectPaletteColor(card, color)
            }
        }
    }

    private fun selectPaletteColor(selectedCard: CardView, color: Int) {
        binding.interactiveGridView.currentColor = color

        colorSwatches.forEach { card ->
            val isSelected = card == selectedCard
            card.scaleX = if (isSelected) 1.25f else 1.0f
            card.scaleY = if (isSelected) 1.25f else 1.0f
        }
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set hint button inactive by default
        binding.btnHint.alpha = 0.5f
        binding.btnHint.setOnClickListener {
            val isShowing = !binding.interactiveGridView.showHintDots
            binding.interactiveGridView.showHintDots = isShowing
            binding.interactiveGridView.invalidate()
            binding.btnHint.alpha = if (isShowing) 1.0f else 0.5f
        }


        binding.btnClear.setOnClickListener {
            binding.interactiveGridView.resetGrid()
        }
    }

    private fun setupNavigation() {
        binding.btnPrevModel.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadCurrentModel()
            }
        }

        binding.btnNextModel.setOnClickListener {
            if (currentIndex < modelsList.size - 1) {
                currentIndex++
                loadCurrentModel()
            }
        }
    }

    private fun setupCompletionListener() {
        //  Shake on wrong color
        binding.interactiveGridView.onWrongColor = {
            playErrorShakeAnimation()
        }

        //  Celebrate on 100% completion
        binding.interactiveGridView.onCompleted = {
            voiceAssistant.speakSuccess()
            ExcellentBannerDialogFragment.show(supportFragmentManager) {
                if (currentIndex < modelsList.size - 1) {
                    currentIndex++
                    loadCurrentModel()
                } else {
                    finish()
                }
            }
        }
    }

    private fun playErrorShakeAnimation() {
        // Playful "Oops!" voice praise
        voiceAssistant.speakError()

        //  Play cartoon wobble shake on the grid card
        val card = binding.cardInteractiveGrid
        card.animate()
            .translationXBy(20f)
            .setDuration(50)
            .withEndAction {
                card.animate()
                    .translationXBy(-40f)
                    .setDuration(80)
                    .withEndAction {
                        card.animate()
                            .translationXBy(30f)
                            .setDuration(70)
                            .withEndAction {
                                card.animate()
                                    .translationX(0f)
                                    .setDuration(60)
                                    .start()
                            }
                            .start()
                    }
                    .start()
            }
            .start()
    }


}