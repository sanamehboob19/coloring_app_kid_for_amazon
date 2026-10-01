package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivitySettingsBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity :
    BaseActivity<ActivitySettingsBinding>(ActivitySettingsBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        binding.root.setBackgroundResource(if (isLandscape) R.drawable.app_doddle_bg_landscape else R.drawable.app_doddle_bg)

        setupTopHeader()
        loadInitialStates()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun setupTopHeader() {
        binding.btnClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadInitialStates() {
        //  Load Background Music state
        val isMusicOn = MusicManager.isMusicEnabled(this)
        binding.switchMusic.isChecked = isMusicOn

        //  Load TTS Voice / Sound Effects state from TinyDB
        val isSoundOn = tinyDB.getBoolean(Constant.KEY_SOUND_ENABLED, true)
        binding.switchSound.isChecked = isSoundOn
    }

    private fun setupListeners() {
        // Background Music Switch Listener
        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            // Toggle music using your MusicManager utility
            val currentState = MusicManager.isMusicEnabled(this)
            if (isChecked != currentState) {
                MusicManager.toggleMusic(this)
            }
            ToastUtils.show(if (isChecked) "Background Music Enabled 🎵" else "Background Music Muted 🔇")
        }

        // TTS Voice / Sound Effects Switch Listener
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            tinyDB.putBoolean(Constant.KEY_SOUND_ENABLED, isChecked)
            ToastUtils.show(if (isChecked) "Voice & Sound FX Enabled 🔊" else "Voice & Sound FX Muted 🤫")
        }

        // Rate Us Button Action
        binding.layoutRateUs.setOnClickListener {
            val appPackageName = packageName
            try {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                )
            }
        }

        // Share App Button Action
        binding.layoutShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                putExtra(Intent.EXTRA_TEXT, "Check out ${getString(R.string.app_name)} - The best coloring and tracing game for kids on Google Play!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share with friends"))
        }

        binding.layoutPrivacyPolicy.setOnClickListener {
            val privacyUrl = "https://sites.google.com/view/coloringgamesforkidsdoodle/home"
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl))
                startActivity(intent)
            } catch (e: Exception) {
                ToastUtils.show("Unable to open browser")
            }
        }


    }
}