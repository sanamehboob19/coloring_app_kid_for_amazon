package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model


import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollectionModel(
    val id: String,
    val title: String,
    @DrawableRes val previewRes: Int,
    @ColorRes val spineColorRes: Int = R.color.kids_primary,
    val isNew: Boolean = false,
    val categoryKey: String = "general"
) : Parcelable