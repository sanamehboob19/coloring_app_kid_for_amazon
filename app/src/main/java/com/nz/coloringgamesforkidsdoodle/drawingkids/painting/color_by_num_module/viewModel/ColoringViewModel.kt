package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.viewModel

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ImageData
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.repository.ImageRepository
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.sealed.NetworkState
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.NetworkConnectivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColoringViewModel @Inject constructor(
    application: Application,
    private val repository: ImageRepository
) : AndroidViewModel(application)
{

    // It is a read-only stream. Updating the DB will update this UI list automatically ONCE.
    val images: StateFlow<List<ImageData.Image>> = repository.getImages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    //  NETWORK STATE: To handle Loading/Error dialogs
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    //  SCROLL STATE: To save Recycler position
    private var recyclerState: Parcelable? = null

    init {
        // Run logic once when ViewModel is created
        checkAndSyncData()
    }

    /**
     * Handles the logic for Scenario 1, 2, and 3.
     */
    private fun checkAndSyncData() {
        viewModelScope.launch {
            val isInternet = NetworkConnectivity.isInternetAvailable(getApplication())
            val hasLocalData = repository.getDbCount() > 0

            when {
                // Scenario 1: First time + No Internet
                !hasLocalData && !isInternet -> {
                    _networkState.value = NetworkState.NoInternet
                }

                // Scenario 2: First time + Has Internet
                !hasLocalData && isInternet -> {
                    fetchFromRemote()
                }

                // Scenario 3: Has Data + Has Internet (Silent Update in background)
                hasLocalData && isInternet -> {
                    repository.fetchAndSaveImages()
                }

                // Scenario 4: Has Data + No Internet
                else -> {
                    // Do nothing, the 'images' StateFlow will automatically show local data
                    _networkState.value = NetworkState.Success
                }
            }
        }
    }

    private suspend fun fetchFromRemote() {
        _networkState.value = NetworkState.Loading
        try {
            repository.fetchAndSaveImages()
            _networkState.value = NetworkState.Success
        } catch (e: Exception) {
            _networkState.value = NetworkState.Error(e.message ?: "Sync Failed")
        }
    }

    // --- UI ACTIONS ---

    fun retryFetchRemote() {
        checkAndSyncData()
    }

    fun resetNetworkState() {
        _networkState.value = NetworkState.Idle
    }

    fun saveScrollState(state: Parcelable?) {
        recyclerState = state
    }

    fun resetProgress(id: Int) {
        viewModelScope.launch {
            repository.resetProgress(id)
        }
    }


    fun getScrollState(): Parcelable? = recyclerState

    fun updateSketchProgress(id: Int, sketchUrl: String, filledIds: List<Int>, isAllFilled: Boolean) {
        viewModelScope.launch {
            repository.updateSketchProgress(id, sketchUrl, filledIds, isAllFilled)
        }
    }
}