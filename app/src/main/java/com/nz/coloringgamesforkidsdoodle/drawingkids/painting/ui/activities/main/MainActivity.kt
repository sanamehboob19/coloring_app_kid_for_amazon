package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.main

import android.os.Bundle
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.act.ColoringGalleryActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.enumms.GameMode
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.CategoryModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityMainBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.ExitDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.abcTracing.AlphabetSelectionActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.blankCanvas.BlankCanvasActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.glowPen.GlowPenCanvasActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.magicPainting.MagicSelectionActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.numberTracing.NumberSelectionActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.pixelArt.PixelSelectionActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.savedWork.SavedArtActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.simpleColoring.CollectionSelectionActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.CategoryAdapter
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.ParentalGateDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm.MainViewModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>
    (ActivityMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()


    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        binding.root.setBackgroundResource(if (isLandscape) R.drawable.app_doddle_bg_landscape else R.drawable.app_doddle_bg)

        binding.cardSavedArtBanner?.setOnClickListener {
            startActivity(Intent(this, SavedArtActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ExitDialogFragment.show(supportFragmentManager) {
                    finishAffinity() // Closes the app completely
                }
            }
        })


        setupTopHeader()
        setupRecyclerView()
        observeViewModel()
        setupBottomDockButton()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeForCurrentScreen(this)
    }

    private fun setupTopHeader() {
        binding.btnClose?.setOnClickListener {
            ExitDialogFragment.show(supportFragmentManager) {
                finishAffinity()
            }
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            onCategorySelected(category)
        }

        // 📐 Adaptive grid: 2 columns in Portrait, 3 columns in Landscape
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val spanCount = if (isLandscape) 3 else 2

        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(this@MainActivity, spanCount)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { list ->
                    categoryAdapter.submitList(list)
                }
            }
        }
    }

    private fun onCategorySelected(category: CategoryModel) {
        when (category.id) {
            GameMode.COLORING_BOOK -> {
                val intent = Intent(this, CollectionSelectionActivity::class.java)
                startActivity(intent)
            }
            GameMode.BLANK_CANVAS -> {
                val intent = Intent(this, BlankCanvasActivity::class.java)
                startActivity(intent)
            }
            GameMode.GLOW_PEN -> {
                val intent = Intent(this, GlowPenCanvasActivity::class.java)
                startActivity(intent)
            }
            GameMode.MAGIC_PAINTING -> {
                val intent = Intent(this, MagicSelectionActivity::class.java)
                startActivity(intent)
            }
            GameMode.NUMBER_TRACING -> {
                val intent = Intent(this, NumberSelectionActivity::class.java)
                startActivity(intent)
            }
            GameMode.ALPHABET_TRACING -> {
                val intent = Intent(this, AlphabetSelectionActivity::class.java)
                startActivity(intent)
            }
            GameMode.PIXEL_ART -> {
                val intent = Intent(this, PixelSelectionActivity::class.java)
                startActivity(intent)
            }
            GameMode.COLOR_BY_NUMBER -> {
                val intent = Intent(this, ColoringGalleryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupBottomDockButton() {
        binding.cardBottomDock?.setOnClickListener {
            ParentalGateDialogFragment.show(supportFragmentManager)
        }
    }
}

