package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PixelArtModel(
    val id: Int,
    val title: String,
    val gridSize: Int = 10,                 // 10x10 grid
    val paletteColorsHex: List<String>,     // Colors available at the bottom
    val matrix: List<Int>                   // 100 cells (10x10 flat list of color indices)
) : Parcelable