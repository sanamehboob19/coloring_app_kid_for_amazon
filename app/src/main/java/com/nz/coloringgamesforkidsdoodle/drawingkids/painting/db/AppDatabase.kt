package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db.CollectionDao
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db.ColorByNumberConverters
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db.ImageDao
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.CollectionColorByNumModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ImageData
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.ColoringTemplateEntity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.MagicTemplateEntity

@Database(
    entities = [
        ColoringTemplateEntity::class,
        MagicTemplateEntity::class,
        ImageData.Image::class,
        CollectionColorByNumModel::class

    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(ColorByNumberConverters::class)


abstract class AppDatabase : RoomDatabase() {

    abstract fun coloringTemplateDao(): ColoringTemplateDao
    abstract fun magicTemplateDao(): MagicTemplateDao

    // color by number
    abstract fun imageDao(): ImageDao
    abstract fun collectionDao(): CollectionDao
}