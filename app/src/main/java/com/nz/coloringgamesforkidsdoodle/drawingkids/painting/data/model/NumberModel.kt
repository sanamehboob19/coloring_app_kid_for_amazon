package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NumberModel(
    val number: Int,             // e.g. 1, 8, 10
    val text: String,           // "1", "8", "10"
    val word: String,           // "One", "Eight", "Ten"
    val glitterColorHex: String // Color for glitter fill
) : Parcelable