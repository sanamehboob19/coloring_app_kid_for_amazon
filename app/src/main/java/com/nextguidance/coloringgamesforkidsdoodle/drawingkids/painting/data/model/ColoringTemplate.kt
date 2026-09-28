package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

/**
 * Model representing a single coloring picture/sheet.
 * Parcelable allows passing the selected picture directly via Intent.
 */


@Parcelize
data class ColoringTemplate(
    val id: Int,
    val title: String,
    @DrawableRes val previewRes: Int = 0,
    val previewUrl: String = "",
    val category: String = "General",
    val isLocked: Boolean = false
) : Parcelable