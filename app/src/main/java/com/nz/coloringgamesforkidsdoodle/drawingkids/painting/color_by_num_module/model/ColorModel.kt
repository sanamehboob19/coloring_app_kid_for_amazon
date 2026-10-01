package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model

data class ColorModel(
    val number: Int,        // The Color ID (1, 2, 3...)
    val color: Int,
    val isSelected: Boolean = false,
    val filledCount: Int = 0,
    val totalCount: Int = 0,
    val isCompleted: Boolean = false
)