package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.pixelArt

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.PixelArtModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityPixelSelectionBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.PixelSelectionAdapter
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders.PixelArtProvider
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PixelSelectionActivity :
    BaseActivity<ActivityPixelSelectionBinding>
        (ActivityPixelSelectionBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTopHeader()
        setupRecyclerView()
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

    private fun setupRecyclerView() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val spanCount = if (isLandscape) 3 else 2

        binding.rvPixelModels.apply {
            layoutManager = GridLayoutManager(this@PixelSelectionActivity, spanCount)
            adapter = PixelSelectionAdapter(PixelArtProvider.models) { selectedModel ->
                onModelSelected(selectedModel)
            }
            setHasFixedSize(true)
        }
    }

    private fun onModelSelected(model: PixelArtModel) {
        val intent = Intent(this, PixelArtActivity::class.java).apply {
            putExtra(EXTRA_SELECTED_MODEL, model)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_SELECTED_MODEL = "extra_selected_model"
    }
}