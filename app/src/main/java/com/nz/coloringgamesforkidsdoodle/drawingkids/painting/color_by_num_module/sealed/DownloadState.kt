package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.sealed

import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.Template

sealed class DownloadState {
    object Idle : DownloadState()
    object Paused : DownloadState()
    data class Downloading(val progress: Int, val downloadedBytes: Long, val totalBytes: Long) : DownloadState()
    data class Processing(val message: String) : DownloadState()

    // ✅ New parsing states
    object ParsingStarted : DownloadState()
    data class ParsingProgress(val message: String, val step: Int, val totalSteps: Int) : DownloadState()

    data class Success(
        val svgBytes: ByteArray,
        val templates: List<Template>?,
        val filledIds: List<Int> = emptyList()
    ) : DownloadState()

    data class Parsed(
        val svgBytes: ByteArray,
        val templates: List<Template>?,
        val filledIds: List<Int> = emptyList()
    ) : DownloadState()

    data class Failed(val error: String, val throwable: Throwable? = null) : DownloadState()
    object Cancelled : DownloadState()
}