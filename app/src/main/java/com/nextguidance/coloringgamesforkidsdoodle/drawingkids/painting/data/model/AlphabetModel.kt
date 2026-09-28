package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlphabetModel(
    val letter: String,        // "A", "B", "C"
    val word: String,          // "Apple", "Ball", "Cat"
    val glitterColorHex: String
) : Parcelable