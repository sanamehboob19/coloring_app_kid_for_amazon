package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.CollectionColorByNumModel
import kotlinx.coroutines.flow.Flow


@Dao
interface CollectionDao {

    @Query("SELECT * FROM collections")
    fun getAllImages(): Flow<List<CollectionColorByNumModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<CollectionColorByNumModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: CollectionColorByNumModel)

    @Query("DELETE FROM collections")
    suspend fun deleteAllImages()

    @Delete
    suspend fun deleteImage(image: CollectionColorByNumModel)

    @Delete
    suspend fun deleteImage(images: List<CollectionColorByNumModel>)
}