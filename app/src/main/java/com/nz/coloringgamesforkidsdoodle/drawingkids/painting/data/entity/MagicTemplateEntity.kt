package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "magic_templates_table")
data class MagicTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val templateId: Int,
    val title: String,
    val previewUrl: String,
    val category: String
) : Parcelable