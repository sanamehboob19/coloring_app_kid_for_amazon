package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.sealed

sealed class NetworkState {
    object Idle : NetworkState()
    object Loading : NetworkState()
    object Success : NetworkState()
    object NoInternet : NetworkState()
    data class Error(val message: String) : NetworkState()
}