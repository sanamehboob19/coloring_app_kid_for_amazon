package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.ColoringTemplateEntity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.repository.ColoringRepository
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.NetworkConnectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateSelectionViewModel @Inject constructor(
    private val repository: ColoringRepository
) : ViewModel() {

    private val _templates = MutableStateFlow<List<ColoringTemplateEntity>>(emptyList())
    val templates: StateFlow<List<ColoringTemplateEntity>> = _templates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showNoInternetDialog = MutableStateFlow(false)
    val showNoInternetDialog: StateFlow<Boolean> = _showNoInternetDialog.asStateFlow()

    fun loadTemplates(context: Context, categoryKey: String?) {
        viewModelScope.launch {
            val hasLocal = repository.hasLocalData()

            if (!hasLocal) {
                if (!NetworkConnectivity.isInternetAvailable(context)) {
                    _showNoInternetDialog.value = true
                    return@launch
                }
            }

            // Sync from github if online
            if (NetworkConnectivity.isInternetAvailable(context)) {
                _isLoading.value = true
                repository.fetchAndSyncTemplates()
                _isLoading.value = false
            }

            // Observe from local DB
            val flow = if (!categoryKey.isNullOrBlank()) {
                repository.getTemplatesByCategory(categoryKey)
            } else {
                repository.allTemplates
            }

            flow.collectLatest { entities ->
                _templates.value = entities
            }
        }
    }

    fun retryLoading(context: Context, categoryKey: String?) {
        _showNoInternetDialog.value = false
        loadTemplates(context, categoryKey)
    }
}