package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.simpleColoring

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.CollectionModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityCollectionSelectionBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.CollectionAdapter
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm.CollectionSelectionViewModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class CollectionSelectionActivity : BaseActivity<ActivityCollectionSelectionBinding>(ActivityCollectionSelectionBinding::inflate) {

    private val viewModel: CollectionSelectionViewModel by viewModels()
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private val snapHelper = PagerSnapHelper()
    private val activeAnimators = mutableListOf<ObjectAnimator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTopHeader()
        setupRecyclerView()
        setupArrowNavigation()
        startContinuousArrowAnimations()
        observeViewModel()
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
        collectionAdapter = CollectionAdapter { collection ->
            onCollectionSelected(collection)
        }

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val scrollOrientation = if (isLandscape) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL

        layoutManager = LinearLayoutManager(this, scrollOrientation, false)

        binding.rvCollections.apply {
            this.layoutManager = this@CollectionSelectionActivity.layoutManager
            adapter = collectionAdapter
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    applyCenterScaling()
                }
            })
        }

        snapHelper.attachToRecyclerView(binding.rvCollections)
    }

    private fun applyCenterScaling() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val totalSpan = if (isLandscape) binding.rvCollections.width / 2f else binding.rvCollections.height / 2f
        if (totalSpan <= 0) return

        for (i in 0 until binding.rvCollections.childCount) {
            val child = binding.rvCollections.getChildAt(i)
            val childCenter = if (isLandscape) {
                (child.left + child.right) / 2f
            } else {
                (child.top + child.bottom) / 2f
            }

            val distanceFromCenter = abs(totalSpan - childCenter)
            val fraction = (distanceFromCenter / totalSpan).coerceIn(0f, 1f)

            val scale = 1.05f - (fraction * 0.27f)
            val alpha = 1.0f - (fraction * 0.35f)

            child.scaleX = scale
            child.scaleY = scale
            child.alpha = alpha
        }
    }

    private fun setupArrowNavigation() {
        val onPreviousClick = {
            val totalItems = collectionAdapter.itemCount
            if (totalItems > 1) {
                val currentPos = getSnappedPosition()
                val targetPos = if (currentPos <= 0) totalItems - 1 else currentPos - 1
                binding.rvCollections.smoothScrollToPosition(targetPos)
            }
        }

        val onNextClick = {
            val totalItems = collectionAdapter.itemCount
            if (totalItems > 1) {
                val currentPos = getSnappedPosition()
                val targetPos = (currentPos + 1) % totalItems
                binding.rvCollections.smoothScrollToPosition(targetPos)
            }
        }

        binding.btnArrowUp?.setOnClickListener { onPreviousClick() }
        binding.btnArrowDown?.setOnClickListener { onNextClick() }
    }

    private fun getSnappedPosition(): Int {
        val centerView = snapHelper.findSnapView(layoutManager) ?: return 0
        return layoutManager.getPosition(centerView)
    }

    private fun startContinuousArrowAnimations() {
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()

        binding.btnArrowUp?.let {
            val anim = ObjectAnimator.ofFloat(it, View.TRANSLATION_Y, 0f, -12f)
            setupBouncingLoop(anim)
        }
        binding.btnArrowDown?.let {
            val anim = ObjectAnimator.ofFloat(it, View.TRANSLATION_Y, 0f, 12f)
            setupBouncingLoop(anim)
        }

    }

    private fun setupBouncingLoop(animator: ObjectAnimator) {
        animator.apply {
            duration = 650
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        activeAnimators.add(animator)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collections.collect { list ->
                    collectionAdapter.submitList(list) {
                        binding.rvCollections.post { applyCenterScaling() }
                    }
                }
            }
        }
    }

    private fun onCollectionSelected(collection: CollectionModel) {
        val intent = Intent(this, TemplateSelectionActivity::class.java).apply {
            putExtra(TemplateSelectionActivity.Companion.EXTRA_COLLECTION_KEY, collection.categoryKey)
            putExtra(TemplateSelectionActivity.Companion.EXTRA_COLLECTION_TITLE, collection.title)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        activeAnimators.forEach { it.cancel() }
        activeAnimators.clear()
    }

    companion object {
        const val EXTRA_COLLECTION_KEY = "extra_collection_key"
        const val EXTRA_COLLECTION_TITLE = "extra_collection_title"
    }
}