package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.ColoringTemplateEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ColoringTemplateDao {
    @Query("SELECT * FROM coloring_templates_table")
    fun getAllTemplates(): Flow<List<ColoringTemplateEntity>>

    @Query("SELECT * FROM coloring_templates_table WHERE category = :categoryKey")
    fun getTemplatesByCategory(categoryKey: String): Flow<List<ColoringTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<ColoringTemplateEntity>)

    @Query("DELETE FROM coloring_templates_table")
    suspend fun deleteAllTemplates()

    @Query("SELECT COUNT(*) FROM coloring_templates_table")
    suspend fun getCount(): Int
}