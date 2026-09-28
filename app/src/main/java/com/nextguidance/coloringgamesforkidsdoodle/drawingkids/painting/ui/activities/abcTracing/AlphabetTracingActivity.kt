package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.abcTracing

import android.os.Build
import android.os.Bundle
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.AlphabetModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityAlphabetTracingBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders.AlphabetProvider
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog.ExcellentBannerDialogFragment
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.VoiceAssistant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlphabetTracingActivity :
    BaseActivity<ActivityAlphabetTracingBinding>
        (ActivityAlphabetTracingBinding::inflate) {

    @Inject
    lateinit var voiceAssistant: VoiceAssistant

    private var currentIndex = 0
    private val lettersList = AlphabetProvider.letters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractInitialLetter()
        setupTopBar()
        setupBottomControls()
        setupTracingCallbacks()
        loadCurrentLetter()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun extractInitialLetter() {
        val selected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                AlphabetSelectionActivity.EXTRA_SELECTED_LETTER,
                AlphabetModel::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(AlphabetSelectionActivity.EXTRA_SELECTED_LETTER)
        }

        currentIndex = if (selected != null) {
            lettersList.indexOfFirst { it.letter == selected.letter }.coerceAtLeast(0)
        } else {
            0
        }
    }

    private fun loadCurrentLetter() {
        val current = lettersList[currentIndex]

        binding.tvLetterTitle.text = "Letter ${current.letter} for ${current.word} ⭐"
        binding.alphabetTracingView.loadLetter(current)

        // Pronounce letter via TTS
        voiceAssistant.speakLetter(current.letter)

        binding.btnPrevLetter.isEnabled = currentIndex > 0
        binding.btnPrevLetter.alpha = if (currentIndex > 0) 1.0f else 0.4f

        binding.btnNextLetter.isEnabled = currentIndex < lettersList.size - 1
        binding.btnNextLetter.alpha = if (currentIndex < lettersList.size - 1) 1.0f else 0.4f
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnReset.setOnClickListener {
            loadCurrentLetter()
        }
    }

    private fun setupBottomControls() {
        binding.btnPrevLetter.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadCurrentLetter()
            }
        }

        binding.btnNextLetter.setOnClickListener {
            if (currentIndex < lettersList.size - 1) {
                currentIndex++
                loadCurrentLetter()
            }
        }
    }

    private fun setupTracingCallbacks() {
        binding.alphabetTracingView.onCompleted = {
            voiceAssistant.speakSuccess()

            ExcellentBannerDialogFragment.show(supportFragmentManager) {
                if (currentIndex < lettersList.size - 1) {
                    currentIndex++
                    loadCurrentLetter()
                } else {
                    finish()
                }
            }
        }
    }
}