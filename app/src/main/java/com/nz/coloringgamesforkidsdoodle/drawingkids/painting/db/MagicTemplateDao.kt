package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.MagicTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MagicTemplateDao {
    @Query("SELECT * FROM magic_templates_table")
    fun getAllMagicTemplates(): Flow<List<MagicTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMagicTemplates(templates: List<MagicTemplateEntity>)

    @Query("DELETE FROM magic_templates_table")
    suspend fun deleteAllMagicTemplates()

    @Query("SELECT COUNT(*) FROM magic_templates_table")
    suspend fun getCount(): Int
}