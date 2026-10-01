package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

data class ImageData(
    val dataVersion: Int,
    val images: List<Image>
)
{
    @Parcelize
    @Entity(tableName = "images")
    data class Image(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val label: String = "",
        val category: String = "",
        val originalUrl: String = "",
        val sketchUrl: String = "",
        val svgUrl: String = "",
        val filled: List<Int> = emptyList<Int>(),
        val isComplete: Boolean = false,
        val premium: Boolean = false,
        val isFavorite: Boolean = false,
        val addedTime: Long = System.currentTimeMillis()
    ) : Parcelable
}