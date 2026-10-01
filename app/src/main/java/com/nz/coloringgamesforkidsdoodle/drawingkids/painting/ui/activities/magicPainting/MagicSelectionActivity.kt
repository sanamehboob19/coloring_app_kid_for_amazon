package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.magicPainting

import android.os.Bundle

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.MagicTemplateEntity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivityMagicSelectionBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.MagicSelectionAdapter
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.LoadingDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog.NoInternetDialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm.MagicSelectionViewModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.MusicManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MagicSelectionActivity : BaseActivity<ActivityMagicSelectionBinding>(ActivityMagicSelectionBinding::inflate) {

    private val viewModel: MagicSelectionViewModel by viewModels()
    private lateinit var adapter: MagicSelectionAdapter
    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTopHeader()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadMagicTemplates(this)
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
        adapter = MagicSelectionAdapter { template ->
            onTemplateSelected(template)
        }

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val spanCount = if (isLandscape) 3 else 2

        binding.rvTemplates.apply {
            layoutManager = GridLayoutManager(this@MagicSelectionActivity, spanCount)
            this.adapter = this@MagicSelectionActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.templates.collect { list ->
                        adapter.submitList(list)
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
                                viewModel.retryLoading(this@MagicSelectionActivity)
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

    private fun onTemplateSelected(template: MagicTemplateEntity) {
        val intent = Intent(this, MagicPaintingActivity::class.java).apply {
            putExtra(EXTRA_MAGIC_ENTITY, template)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_MAGIC_ENTITY = "extra_magic_entity"
    }
}