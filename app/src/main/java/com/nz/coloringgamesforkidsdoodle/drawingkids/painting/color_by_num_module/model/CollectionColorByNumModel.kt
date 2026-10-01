package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionColorByNumModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val image: String = ""
)