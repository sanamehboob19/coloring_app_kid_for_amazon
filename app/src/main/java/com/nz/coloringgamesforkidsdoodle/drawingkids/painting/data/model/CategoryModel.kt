package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model


import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.enumms.GameMode

data class CategoryModel(
    val id: GameMode,
    val title: String,
    @DrawableRes val iconRes: Int,
    @ColorRes val borderColorRes: Int
)