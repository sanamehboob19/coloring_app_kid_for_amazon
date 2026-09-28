package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.numberTracing

import android.os.Build
import android.os.Bundle
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.NumberModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityNumberTracingBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders.NumberProvider
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog.ExcellentBannerDialogFragment
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.VoiceAssistant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NumberTracingActivity :
    BaseActivity<ActivityNumberTracingBinding>(ActivityNumberTracingBinding::inflate)
{

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    private var currentIndex = 0
    private val numbersList = NumberProvider.numbers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractInitialNumber()
        setupTopBar()
        setupBottomControls()
        setupTracingCallbacks()
        loadCurrentNumber()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun extractInitialNumber() {
        val selected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                NumberSelectionActivity.EXTRA_SELECTED_NUMBER,
                NumberModel::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NumberSelectionActivity.EXTRA_SELECTED_NUMBER)
        }

        currentIndex = if (selected != null) {
            numbersList.indexOfFirst { it.number == selected.number }.coerceAtLeast(0)
        } else {
            0
        }
    }

    private fun loadCurrentNumber() {
        val current = numbersList[currentIndex]

        binding.tvNumberTitle.text = "Number ${current.word} ⭐"
        binding.numberTracingView.loadNumber(current)

        // Pronounce number via TTS
        voiceAssistant.speakNumber(current.text)

        binding.btnPrevNumber.isEnabled = currentIndex > 0
        binding.btnPrevNumber.alpha = if (currentIndex > 0) 1.0f else 0.4f

        binding.btnNextNumber.isEnabled = currentIndex < numbersList.size - 1
        binding.btnNextNumber.alpha = if (currentIndex < numbersList.size - 1) 1.0f else 0.4f
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnReset.setOnClickListener {
            loadCurrentNumber()
        }
    }

    private fun setupBottomControls() {
        binding.btnPrevNumber.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadCurrentNumber()
            }
        }

        binding.btnNextNumber.setOnClickListener {
            if (currentIndex < numbersList.size - 1) {
                currentIndex++
                loadCurrentNumber()
            }
        }
    }

    private fun setupTracingCallbacks() {
        binding.numberTracingView.onCompleted = {
            val current = numbersList[currentIndex]

            //  Pronounce Number (e.g. "Eight!")
            voiceAssistant.speakSuccess()

            //  Show Festive "EXCELLENT!" Banner
            ExcellentBannerDialogFragment.show(supportFragmentManager) {
                // Auto-advance to next number or return to selection
                if (currentIndex < numbersList.size - 1) {
                    currentIndex++
                    loadCurrentNumber()
                } else {
                    finish()
                }
            }
        }
    }
}