package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.MagicTemplateEntity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.repository.MagicRepository
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.NetworkConnectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MagicSelectionViewModel @Inject constructor(
    private val repository: MagicRepository
) : ViewModel() {

    private val _templates = MutableStateFlow<List<MagicTemplateEntity>>(emptyList())
    val templates: StateFlow<List<MagicTemplateEntity>> = _templates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showNoInternetDialog = MutableStateFlow(false)
    val showNoInternetDialog: StateFlow<Boolean> = _showNoInternetDialog.asStateFlow()

    fun loadMagicTemplates(context: Context) {
        viewModelScope.launch {
            val hasLocal = repository.hasLocalData()

            if (!hasLocal && !NetworkConnectivity.isInternetAvailable(context)) {
                _showNoInternetDialog.value = true
                return@launch
            }

            if (NetworkConnectivity.isInternetAvailable(context)) {
                _isLoading.value = true
                repository.fetchAndSyncMagicTemplates()
                _isLoading.value = false
            }

            repository.allMagicTemplates.collectLatest { list ->
                _templates.value = list
            }
        }
    }

    fun retryLoading(context: Context) {
        _showNoInternetDialog.value = false
        loadMagicTemplates(context)
    }
}