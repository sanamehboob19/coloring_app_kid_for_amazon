package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ColorByNumberConverters {
    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }
}