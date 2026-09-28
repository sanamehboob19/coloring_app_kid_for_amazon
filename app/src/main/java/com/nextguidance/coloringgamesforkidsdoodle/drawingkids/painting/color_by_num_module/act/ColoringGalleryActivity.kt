package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.act

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.adapter.SketchAdapter
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ImageData
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.sealed.NetworkState
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.viewModel.ColoringViewModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityColoringGalleryBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog.NoInternetDialog
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.NetworkConnectivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ColoringGalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColoringGalleryBinding
    private val viewModel: ColoringViewModel by viewModels()
    private lateinit var adapter: SketchAdapter
    private lateinit var layoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColoringGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initObserver()

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun initAdapter() {
        // showNumberLabel = true shows the 1, 2, 3 labels
        adapter = SketchAdapter(onItemClick = { image ->
            onSketchClick(image)
        }, showNumberLabel = true)

        layoutManager = GridLayoutManager(this, 2)
        binding.rvSketches.layoutManager = layoutManager
        binding.rvSketches.adapter = adapter
    }

    private fun initObserver() {

        lifecycleScope.launch {
            viewModel.images.collect { images ->

                if (images.isNotEmpty()) {
                    if (adapter.currentList.isEmpty()) {
                        val randomItems = images.shuffled()
                        Log.d("COLORING_TEST", "Gallery opened: Shuffling ${images.size} items")
                        adapter.submitList(randomItems)
                    }
                }

            }
        }


        lifecycleScope.launch {
            viewModel.networkState.collect { state ->
                when(state) {
                    is NetworkState.NoInternet -> {
                        // Only show dialog if we have NO images to show
                        if (adapter.currentList.isEmpty()) {
                            showNoInternetDialog()
                        }
                        viewModel.resetNetworkState()
                    }
                    is NetworkState.Loading -> { }
                    is NetworkState.Success -> { }
                    is NetworkState.Error -> { }
                    else -> {}
                }
            }
        }


    }


    private fun onSketchClick(image: ImageData.Image) {
        val isInternet = NetworkConnectivity.isInternetAvailable(this)
        val isStarted = image.filled.isNotEmpty()

        if (!isInternet && !isStarted) {
            ToastUtils.show("Internet needed to Download this drawing")
            return
        }

        val finalFilledList = if (image.isComplete) {
            viewModel.resetProgress(image.id)
            ArrayList<Int>()
        } else {
            ArrayList(image.filled)
        }

        //  Tracking which sketch and category is opened
//        recordEvent("coloring_sketch_opened",
//            "sketch_id" to image.id,
//            "category" to image.category
//        )

        val bundle = Bundle().apply {
            putInt(Constant.KEY_SKETCH_ID, image.id)
            putString(Constant.KEY_SKETCH_RES, image.sketchUrl)
            putString(Constant.KEY_SVG_RES, image.svgUrl)
            putString(Constant.KEY_IMAGE_RES, image.originalUrl)
            putIntegerArrayList(Constant.KEY_SKETCH_FILLED_ID, ArrayList(finalFilledList))
        }
        val intent = Intent(this, CanvasActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }


    private fun showNoInternetDialog() {

        val dialog = NoInternetDialog.Companion.newInstance()

        dialog.setOpenSettingsListener {
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            //  When user comes back from settings, auto-retry
            viewModel.retryFetchRemote()
        }

        dialog.setRetryListener {
            if (NetworkConnectivity.isInternetAvailable(this)) {
                viewModel.retryFetchRemote() //  direct retry, no dialog dismiss needed
                dialog.dismiss()
            } else {
                ToastUtils.show("No Internet ")
            }
        }

        dialog.show(supportFragmentManager, "NoInternetDialog")
    }


    private fun saveScrollState() {
        viewModel.saveScrollState(layoutManager.onSaveInstanceState())
    }

    private fun restoreScrollState() {
        viewModel.getScrollState()?.let { state ->
            binding.rvSketches.post {
                layoutManager.onRestoreInstanceState(state)
            }
        }
    }

    override fun onPause() {
        saveScrollState()
        super.onPause()
    }
}