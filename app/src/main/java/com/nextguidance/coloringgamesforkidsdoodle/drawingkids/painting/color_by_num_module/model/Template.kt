package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model

import android.graphics.Bitmap

data class Template(
    val color: String,
    val isStroke: Boolean,
    val items: List<TemplateItem>
)

data class TemplateItem(
    val id: Int,
    val bitmap: Bitmap,
    val offsetX: Float,
    val offsetY: Float,
    val isFilled: Boolean,
    val label: LabelInfo,
)

data class LabelInfo(
    val centerX: Float,
    val centerY: Float,
    val radius: Float
)