package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.abcTracing

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.AlphabetModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityAlphabetSelectionBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.AlphabetSelectionAdapter
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders.AlphabetProvider
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlphabetSelectionActivity :
    BaseActivity<ActivityAlphabetSelectionBinding>
        (ActivityAlphabetSelectionBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTopHeader()
        setupAlphabetGrid()
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

    private fun setupAlphabetGrid() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val spanCount = if (isLandscape) 5 else 4

        binding.rvAlphabets.apply {
            layoutManager = GridLayoutManager(this@AlphabetSelectionActivity, spanCount)
            adapter = AlphabetSelectionAdapter(AlphabetProvider.letters) { selectedLetter ->
                onLetterSelected(selectedLetter)
            }
            setHasFixedSize(true)
        }
    }

    private fun onLetterSelected(letter: AlphabetModel) {
        val intent = Intent(this, AlphabetTracingActivity::class.java).apply {
            putExtra(EXTRA_SELECTED_LETTER, letter)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_SELECTED_LETTER = "extra_selected_letter"
    }
}