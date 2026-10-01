package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.numberTracing

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.NumberModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityNumberSelectionBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.NumberSelectionAdapter
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders.NumberProvider
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NumberSelectionActivity :
    BaseActivity<ActivityNumberSelectionBinding>(ActivityNumberSelectionBinding::inflate)
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTopHeader()
        setupNumbersGrid()
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

    private fun setupNumbersGrid() {
        // 4 columns in portrait, 5 in landscape
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val spanCount = if (isLandscape) 5 else 4

        binding.rvNumbers.apply {
            layoutManager = GridLayoutManager(this@NumberSelectionActivity, spanCount)
            adapter = NumberSelectionAdapter(NumberProvider.numbers) { selectedNumber ->
                onNumberSelected(selectedNumber)
            }
            setHasFixedSize(true)
        }
    }

    private fun onNumberSelected(number: NumberModel) {
        val intent = Intent(this, NumberTracingActivity::class.java).apply {
            putExtra(EXTRA_SELECTED_NUMBER, number)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_SELECTED_NUMBER = "extra_selected_number"
    }
}