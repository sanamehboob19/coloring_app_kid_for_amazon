package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.act

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.adapter.ColorAdapter
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper.IdleBulbController
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper.IdleDetectorHelper
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper.ImageLoader
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper.SvgDownloadManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ColorModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.sealed.DownloadState
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.util.PreferencesManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.viewModel.ColoringViewModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.widget.ColorProgress
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.widget.OnColorLongClickListener
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.widget.OnColorProgressListener
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityCanvasBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.LoadingDialog
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.SaveShareDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.SketchCompletedDialog
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.extension.showConfetti
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

@AndroidEntryPoint
class CanvasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCanvasBinding
    private val viewModel: ColoringViewModel by viewModels()
    private val downloadManager by lazy { SvgDownloadManager(this) }
    private lateinit var adapter: ColorAdapter
    private var isParsed = false
    private var isDialogShow = false
    private var loadingDialog: LoadingDialog? = null
    private lateinit var idleDetector: IdleDetectorHelper
    private lateinit var idleBulbController: IdleBulbController
    private var currentSketchId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanvasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initObserver()
        initListeners()
        setupGame()
        // Handle Back Press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isParsed) finish() else goBack()
            }
        })
    }

    private fun initAdapter() {
        adapter = ColorAdapter { model -> onColorClick(model) }
        binding.rvColor.adapter = adapter

        binding.rvColor.post {
            val progressMap = binding.overlay.getColorProgress()
            val colorIdList = binding.overlay.getColorListWithIds()

            val modelList = colorIdList.map { (id, colorInt) ->
                val progress = progressMap[colorInt] ?: ColorProgress(0, 0)
                val isNowCompleted = progress.filledCount == progress.totalCount && progress.totalCount > 0
                ColorModel(
                    number = id, color = colorInt,
                    isSelected = binding.overlay.isColorSelected(colorInt),
                    filledCount = progress.filledCount,
                    totalCount = progress.totalCount, isCompleted = isNowCompleted
                )
            }

//            initIdleDetection(modelList)
            updateLinearProgress(modelList)
            adapter.submitList(modelList)
//            showLog("TagModelList", "${modelList.size}")

            val firstIncompleteIndex = modelList.indexOfFirst { !it.isCompleted }
            if (firstIncompleteIndex != -1) findNextIncompleteColor(modelList, firstIncompleteIndex - 1)
        }
    }

    private fun initData() {
        currentSketchId = intent.getIntExtra(Constant.KEY_SKETCH_ID, -1)

        val sketchUrl = intent.getStringExtra(Constant.KEY_SKETCH_RES)
        val svgUrl = intent.getStringExtra(Constant.KEY_SVG_RES)
        val originalUrl = intent.getStringExtra(Constant.KEY_IMAGE_RES)

        Log.d("CANVAS_DEBUG", "Sketch: $sketchUrl")
        Log.d("CANVAS_DEBUG", "SVG: $svgUrl")
        Log.d("CANVAS_DEBUG", "Original: $originalUrl")


        Glide.with(this)
            .asBitmap()
            .load(sketchUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .signature(ObjectKey(PreferencesManager.getInt(Constant.KEY_DATA_VERSION)))
            .placeholder(R.color.white)
            .into(binding.imgSketch)
    }

    @SuppressLint("SetTextI18n")
    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                downloadManager.state.collect { state ->
                    when (state) {
                        is DownloadState.Downloading -> {
                            binding.lytLoading.visibility = View.VISIBLE
                            binding.progressBar.progress = state.progress
                            binding.overlay.visibility = View.INVISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                            binding.txtProgress.text = "${state.progress}%  " +
                                    "(${state.downloadedBytes / 1024}KB / ${state.totalBytes / 1024}KB)"
                        }
                        is DownloadState.Processing -> {
                            binding.overlay.visibility = View.INVISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                            binding.txtProgress.text = state.message
                        }
                        is DownloadState.Paused -> {
                            binding.txtProgress.text = "Paused"
                        }
                        is DownloadState.ParsingStarted -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.txtProgress.text = "Loading"
                        }
                        is DownloadState.ParsingProgress -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.VISIBLE
                            binding.imgSketch.visibility = View.VISIBLE
                            binding.btnNext.visibility = View.GONE
                            binding.resetZoomLayout.visibility = View.GONE
                            binding.progressCompleted.visibility = View.GONE
                            binding.txtProgress.text = state.message  // e.g. "Processing paths..."
                        }
                        is DownloadState.Parsed -> {
                            isParsed = true
                            // Drawing loaded and kid started coloring
                            binding.btnBack.visibility = View.VISIBLE
                            binding.btnNext.visibility = View.VISIBLE
                            binding.resetZoomLayout.visibility = View.GONE
                            binding.progressCompleted.visibility = View.VISIBLE
                            binding.konfettiView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            binding.txtProgress.text = "Ready"
                            binding.overlay.visibility = View.VISIBLE

                            state.templates?.let { binding.overlay.setGroupedTemplate(it) }
                            if (state.filledIds.isNotEmpty()) binding.overlay.setFilledIds(state.filledIds)

                            // Load Background Colored Image
                            val originalUrl = intent.getStringExtra(Constant.KEY_IMAGE_RES).toString()
                            ImageLoader.loadBitmap(this@CanvasActivity, originalUrl)?.let { bitmap ->
                                // ONLY set background if the activity is still alive
                                if (!isFinishing && !isDestroyed) {
                                    binding.overlay.setBackgroundBitmapImage(bitmap)
                                }
                            }

                            animateSketchImg()
                            initAdapter()
                        }
                        is DownloadState.Failed -> { binding.progressBar.visibility = View.GONE }
                        is DownloadState.Cancelled -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@CanvasActivity,
                                "Cancelled", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun animateSketchImg() {
        binding.root.post {
            val rootHeight = binding.main.height

            val location = IntArray(2)
            binding.imgSketch.getLocationInWindow(location)
            val rootLocation = IntArray(2)
            binding.main.getLocationInWindow(rootLocation)

            val imageTopRelativeToRoot = location[1] - rootLocation[1]
            val imageHeight = binding.imgSketch.height

            val imageTranslation = ((rootHeight / 2f) - (imageHeight / 2f)) - imageTopRelativeToRoot

            animateColorRv()
            binding.imgSketch.animate()
                .translationY(imageTranslation)
                .setDuration(800)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    binding.lytLoading.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .setInterpolator(DecelerateInterpolator())
                        .withEndAction {
                            binding.lytLoading.visibility = View.GONE
                            binding.lytLoading.alpha = 1f
                        }
                        .start()
                }
                .start()
        }
    }

//    private fun setupGame() {
//        val svgUrl = intent.getStringExtra(Constant.KEY_SVG_RES)
//        val filledList = intent.getIntegerArrayListExtra(Constant.KEY_SKETCH_FILLED_ID) ?: emptyList<Int>()
//        val originalUrl = intent.getStringExtra(Constant.KEY_IMAGE_RES).toString()
//
//        val transparentBitmap = (ContextCompat.getDrawable(this, R.drawable.img_transparent) as BitmapDrawable).bitmap
//
//        lifecycleScope.launch {
//            val sourceBitmap = ImageLoader.loadBitmap(this@CanvasActivity, originalUrl) ?: return@launch
//            downloadManager.downloadAndParse(svgUrl.toString(), transparentBitmap, sourceBitmap, filledList, lifecycleScope)
//        }
//    }


    private fun setupGame() {
        val svgUrl = intent.getStringExtra(Constant.KEY_SVG_RES)
        val filledList = intent.getIntegerArrayListExtra(Constant.KEY_SKETCH_FILLED_ID) ?: emptyList<Int>()
        val originalUrl = intent.getStringExtra(Constant.KEY_IMAGE_RES).toString()

        // 🌟 Safe drawable to bitmap conversion preventing ClassCastException
        val drawable = ContextCompat.getDrawable(this, R.drawable.img_transparent)
        val transparentBitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(drawable?.intrinsicWidth?.takeIf { it > 0 } ?: 100,
                drawable?.intrinsicHeight?.takeIf { it > 0 } ?: 100, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable?.setBounds(0, 0, canvas.width, canvas.height)
            drawable?.draw(canvas)
            bitmap
        }

        lifecycleScope.launch {
            val sourceBitmap = ImageLoader.loadBitmap(this@CanvasActivity, originalUrl) ?: return@launch
            downloadManager.downloadAndParse(svgUrl.toString(), transparentBitmap, sourceBitmap, filledList, lifecycleScope)
        }
    }



    private fun animateColorRv() {
        binding.rvColor.post {  // Set it to visible first
            binding.rvColor.visibility = View.VISIBLE

            val rvHeight = binding.rvColor.height.toFloat().takeIf { it > 0 }
                ?: (resources.displayMetrics.heightPixels * 0.3f)

            // Start from below the screen
            binding.rvColor.translationY = rvHeight
            binding.rvColor.alpha = 0f

            // Animate it sliding up
            binding.rvColor.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(700)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun initListeners() {
        binding.btnBack.setOnClickListener { goBack() }

        binding.btnNext.setOnClickListener {   // Hint Logic
            val success = binding.overlay.findAndZoomToNextPath()
            if (success) {
                // Tracking that the user used a hint
//                recordEvent("coloring_hint_used", "sketch_id" to currentSketchId)
            }
        }

        binding.overlay.onZoomChanged = { isZoomed ->
            binding.resetZoomLayout.isVisible = isZoomed
        }

        binding.resetZoomLayout.setOnClickListener { binding.overlay.resetZoom() }

        binding.overlay.onColorLongClickListener = object : OnColorLongClickListener {
            override fun onColorSelected(color: Int) {
                // Find the model in the adapter that matches this color
                val model = adapter.currentList.find { it.color == color }
                if (model != null) {
                    if (model.isCompleted) {
                        ToastUtils.show("Colored already finished")
                        return
                    }
                    // reuse selection logic
                    onColorClick(model)
                    // Scroll the RecyclerView to the newly selected color
                    val position = adapter.currentList.indexOf(model)
                    if (position != -1) {
                        binding.rvColor.smoothScrollToPosition(position)
                    }
                }
            }
        }

        //  Disable the default blink animation once, when setting up RecyclerView
        (binding.rvColor.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        binding.overlay.colorProgressListener = object : OnColorProgressListener {
            override fun onProgressUpdated(colorProgressMap: Map<Int, ColorProgress>) {
                if (!::adapter.isInitialized) return

                val currentList = adapter.currentList
                var currentSelectedIndex = -1

                val updatedList = currentList.mapIndexed { index, model ->
                    if (model.isSelected) currentSelectedIndex = index

                    val progress = colorProgressMap[model.color] ?: ColorProgress(0, 0)
                    val isNowCompleted = progress.filledCount == progress.totalCount && progress.totalCount > 0

                    model.copy(filledCount = progress.filledCount, totalCount = progress.totalCount, isCompleted = isNowCompleted)
                }

                adapter.updateProgress(updatedList)
                updateLinearProgress(updatedList)

                //  Animate newly completed item
                updatedList.forEachIndexed { index, item ->
                    // Get the old state of this exact item before this update loop
                    val oldItem = currentList.getOrNull(index)
                    val wasAlreadyCompleted = oldItem?.isCompleted == true

                    // Only run if it's finished now, but wasn't finished a millisecond ago
                    if (item.isCompleted && !wasAlreadyCompleted) {
//                        showLog("TagCompleted", "1")
                        animateCompletedItem(index)
                    }
                }

                // Shift color after update if current color completed
                val currentModel = updatedList.getOrNull(currentSelectedIndex)
                if (currentModel != null && currentModel.isCompleted) {
                    findNextIncompleteColor(updatedList, currentSelectedIndex)
                }

            }

            override fun onComplete() {
                //Animate the index here
            }
        }

    }

    private fun animateCompletedItem(position: Int) {
        val viewHolder = binding.rvColor.findViewHolderForAdapterPosition(position) as? ColorAdapter.ViewHolder ?: return
        val model = adapter.currentList.getOrNull(position) ?: return

        // Play the Pop Animation
        viewHolder.playCompletionAnimation(
            model = model,
            onAnimationEnd = {   binding.konfettiView.showConfetti()   }
        )
    }

    private fun updateLinearProgress(list: List<ColorModel>): Int {
        val total = list.sumOf { it.totalCount }
        val filled = list.sumOf { it.filledCount }
        val progress = if (total > 0) (filled * 100) / total else 0
        binding.progressCompleted.progress = progress
        if (progress == 100) {
            //  User successfully finished the whole drawing!
//            recordEvent("coloring_level_finished", "sketch_id" to currentSketchId)

            if (::idleDetector.isInitialized) {
                idleDetector.stop()
            }
            if (::idleBulbController.isInitialized) {
                idleBulbController.stop()
            }
            showSketchCompleteDialog()
        }
        return progress
    }

    private fun showSketchCompleteDialog() {
        if (isFinishing || isDestroyed || isDialogShow) return
        isDialogShow = true
        val dialog = SketchCompletedDialog(this) {
            performAction(isFinishRequest = true)
        }

        dialog.show()
    }


    private fun performAction(isFinishRequest: Boolean = false) {
        if (isFinishing || isDestroyed) return

        val sketchId = intent.getIntExtra(Constant.KEY_SKETCH_ID, -1)
        if (sketchId == -1) {
            finish()
            return
        }

        val isAllFilled = binding.progressCompleted.progress == 100
        val filledIds = binding.overlay.getFilledIds()

        lifecycleScope.launch {
            showLoadingDialog()

            try {
                // 1. Get the final finished colored drawing bitmap directly from the canvas overlay
                val bitmap = withContext(Dispatchers.IO) {
                    binding.overlay.getCurrentDrawingBitmap()
                }

                // 2. Save progress to Room Database
                val uriString = withContext(Dispatchers.IO) {
                    saveBitmapAndGetUri(this@CanvasActivity, bitmap)?.toString()
                }
                viewModel.updateSketchProgress(sketchId, uriString ?: "", filledIds, isAllFilled)

                dismissLoadingDialog()

                if (isFinishRequest) {
                    if (bitmap != null) {
                        // 3. 🌟 Open your existing beautiful Save & Share Dialog!
                        SaveShareDialogFragment.show(supportFragmentManager, bitmap)
                    } else {
                        ToastUtils.show("Failed to generate image")
                        finish()
                    }
                } else {
                    finish()
                }
            } catch (e: Exception) {
                dismissLoadingDialog()
                Log.e("CANVAS_SAVE", "Error saving: ${e.message}")
            }
        }
    }



//    private fun performAction(isFinishRequest: Boolean = false) {
//        if (isFinishing || isDestroyed) return
//
//        //  Get ID from Intent
//        val sketchId = intent.getIntExtra(Constant.KEY_SKETCH_ID, -1)
//        if (sketchId == -1) {
//            finish()
//            return
//        }
//
//        val isAllFilled = binding.progressCompleted.progress == 100
//        val filledIds = binding.overlay.getFilledIds()
//
//        lifecycleScope.launch {
//            showLoadingDialog() // Show "Saving..." feedback
//
//            try {
//                if (filledIds.isNotEmpty()) {
//                    //  Create the final colored image (Bitmap)
//                    val uri = withContext(Dispatchers.IO) {
//                        val bitmap = binding.overlay.getCurrentDrawingBitmap()
//                        saveBitmapAndGetUri(this@CanvasActivity, bitmap).toString()
//                    }
//
//                    //  Save progress to Room Database
//                    viewModel.updateSketchProgress(sketchId, uri, filledIds, isAllFilled)
//                    dismissLoadingDialog()
//
//                    if (isFinishRequest) {
//                        // Go to Download/Share Activity
//                        openDownloadActivity(uri)
//                    } else {
//                        finish() //  go back to Gallery
//                    }
//                } else {
//                    if (isFinishRequest) {
//                        openDownloadActivity(null)
//                    } else {
//                        finish()
//                    }
//                }
//            } catch (e: Exception) {
//                dismissLoadingDialog()
//                Log.e("CANVAS_SAVE", "Error saving: ${e.message}")
//            }
//        }
//    }

    private fun showLoadingDialog() {
        if (isFinishing || isDestroyed) return

        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newInstance("Saving your Sketch")

            if (!supportFragmentManager.isStateSaved) {
                loadingDialog?.show(supportFragmentManager, LoadingDialog.TAG)
            }
        }
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val cacheDir = File(context.cacheDir, "drawings").apply { mkdirs() }
            val file = File(cacheDir, "drawing_${System.currentTimeMillis()}.png")

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            null
        }
    }

//    private fun openDownloadActivity(uri: String?) {
//        val intent = Intent(this, DownloadActivity::class.java).apply {
//            putExtra(Constant.IMAGE_URI, uri)
//            // 🌟 Grant temporary read permission so DownloadActivity can load the file URI
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//        startActivity(intent)
//        finish()
//    }

    private fun onColorClick(model: ColorModel) {
        binding.overlay.setSelectedColor(model.color)
        val updatedList = adapter.currentList.map { it.copy(isSelected = (it.color == model.color)) }
        adapter.submitList(updatedList)
        adapter.setSelectedColor(model.color)
    }

    private fun findNextIncompleteColor(list: List<ColorModel>, currentIndex: Int) {
        val next = list.drop(currentIndex + 1).firstOrNull { !it.isCompleted }
            ?: list.firstOrNull { !it.isCompleted }
        next?.let { onColorClick(it) }
    }

    private fun goBack() {  // Show exit dialog here or just finish
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initIdleDetection(list: List<ColorModel>) {
        idleBulbController = IdleBulbController(
            bulbView = binding.imgIdleBulb, rootView = binding.main,
            onBulbClicked = {
                // Tracking hint used via the idle bulb
//                recordEvent("coloring_hint_used", "sketch_id" to currentSketchId)
                binding.overlay.findAndZoomToNextPath() },
            onCycleComplete = { idleDetector.onUserInteracted() }
        )
        idleDetector = IdleDetectorHelper(onIdle = {
            if (isParsed) idleBulbController.start(lifecycleScope)
        })
        idleDetector.start(lifecycleScope)
        binding.overlay.setOnTouchListener { _, _ ->
            idleDetector.onUserInteracted()
            idleBulbController.stop()
            false
        }
    }

    override fun onDestroy() {
        //  Disconnect the engine from the UI first
        binding.overlay.colorProgressListener = null
        binding.overlay.onColorLongClickListener = null

        //  SAFE GLIDE CLEAR:
        try {
            Glide.with(applicationContext).clear(binding.imgSketch)
            binding.rvColor.adapter = null
        } catch (e: Exception) {
            // Silently catch if anything goes wrong during cleanup
        }

        super.onDestroy()
    }


}