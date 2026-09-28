package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model

data class DownloadConfig(
    val connectTimeoutSec: Long = 30,
    val readTimeoutSec: Long = 60,
    val writeTimeoutSec: Long = 60,
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 2000,
)