package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.simpleColoring

import android.os.Bundle

import android.content.Intent
import android.content.res.Configuration
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.ColoringTemplate
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityTemplateSelectionBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.TemplateAdapter
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.LoadingDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.NoInternetDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm.TemplateSelectionViewModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch



@AndroidEntryPoint
class TemplateSelectionActivity :
    BaseActivity<ActivityTemplateSelectionBinding>
        (ActivityTemplateSelectionBinding::inflate) {

    private val viewModel: TemplateSelectionViewModel by viewModels()
    private lateinit var templateAdapter: TemplateAdapter
    private var loadingDialog: LoadingDialogFragment? = null
    private var categoryKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryKey = intent.getStringExtra(CollectionSelectionActivity.EXTRA_COLLECTION_KEY)

        setupTopHeader()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadTemplates(this, categoryKey)
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
        templateAdapter = TemplateAdapter { template ->
            onTemplateSelected(template)
        }

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val spanCount = if (isLandscape) 3 else 2

        binding.rvTemplates.apply {
            layoutManager = GridLayoutManager(this@TemplateSelectionActivity, spanCount)
            adapter = templateAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.templates.collect { list ->
                        val uiModels = list.map { entity ->
                            ColoringTemplate(
                                id = entity.templateId,
                                title = entity.title,
                                previewRes = com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R.drawable.a1, // fallback
                                previewUrl = entity.previewUrl, // passing string URL
                                category = entity.category,
                                isLocked = entity.isLocked
                            )
                        }

                        templateAdapter.submitList(uiModels)

                        if (uiModels.isEmpty()) {
                            binding.tvEmptyState?.visibility = View.VISIBLE
                            binding.rvTemplates.visibility = View.GONE
                        } else {
                            binding.tvEmptyState?.visibility = View.GONE
                            binding.rvTemplates.visibility = View.VISIBLE
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        if (isLoading) showLoadingDialog() else dismissLoadingDialog()
                    }
                }

                launch {
                    viewModel.showNoInternetDialog.collect { show ->
                        if (show) {
                            NoInternetDialogFragment.show(supportFragmentManager) {
                                viewModel.retryLoading(this@TemplateSelectionActivity, categoryKey)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null && !supportFragmentManager.isStateSaved) {
            loadingDialog = LoadingDialogFragment.newInstance()
            loadingDialog?.show(supportFragmentManager, LoadingDialogFragment.TAG)
        }
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun onTemplateSelected(template: ColoringTemplate) {
        val intent = Intent(this, ColoringCanvasActivity::class.java).apply {
            putExtra(EXTRA_TEMPLATE, template)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_TEMPLATE = "extra_template"
        const val EXTRA_COLLECTION_KEY = "extra_collection_key"
        const val EXTRA_COLLECTION_TITLE = "extra_collection_title"
    }
}



