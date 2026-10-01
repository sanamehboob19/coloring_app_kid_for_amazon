package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coloring_templates_table")
data class ColoringTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val templateId: Int,
    val title: String,
    val previewUrl: String,
    val category: String,
    val isLocked: Boolean = false
)