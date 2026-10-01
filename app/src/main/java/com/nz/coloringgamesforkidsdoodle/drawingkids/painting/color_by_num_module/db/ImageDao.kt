package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ImageData
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    // Retrieve all images
    @Query("SELECT * FROM images WHERE label != 'Banner'")
    fun getAllImages(): Flow<List<ImageData.Image>>

    @Query("SELECT * FROM images WHERE label != 'Banner' ORDER BY addedTime DESC")
    fun getDailyImages(): Flow<List<ImageData.Image>>

    @Query("SELECT COUNT(*) FROM images")
    suspend fun getCount(): Int

    // Retrieve images by category
    @Query("SELECT * FROM images WHERE category = :category")
    fun getImagesByCategory(category: String): Flow<List<ImageData.Image>>

    // Retrieve images by label
    @Query("SELECT * FROM images WHERE label = :label")
    fun getImagesByLabel(label: String): Flow<List<ImageData.Image>>

    // Retrieve only premium images
    @Query("SELECT * FROM images WHERE premium = 1")
    fun getPremiumImages(): Flow<List<ImageData.Image>>

    // Retrieve only free images
    @Query("SELECT * FROM images WHERE premium = 0")
    fun getFreeImages(): Flow<List<ImageData.Image>>

    // Retrieve all unique categories
    @Query("SELECT DISTINCT category FROM images")
    fun getAllCategories(): Flow<List<String>>

    // Retrieve all unique labels
    @Query("SELECT DISTINCT label FROM images")
    fun getAllLabels(): Flow<List<String>>

    // Insert a list of images (replace on conflict)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageData.Image>)

    // Insert a single image
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageData.Image)

    // Delete all images
    @Query("DELETE FROM images")
    suspend fun clearImages()

    // Delete images by category
    @Query("DELETE FROM images WHERE category = :category")
    suspend fun deleteImagesByCategory(category: String)

    // Delete images by label
    @Query("DELETE FROM images WHERE label = :label")
    suspend fun deleteImagesByLabel(label: String)


    @Query("UPDATE images SET isFavorite = 1 WHERE id = :imageId")
    suspend fun addToFavourite(imageId: Int)

    @Query("UPDATE images SET isFavorite = 0 WHERE id = :imageId")
    suspend fun removeFromFavourite(imageId: Int)

    @Update
    suspend fun updateSketch(model: ImageData.Image)

    @Query("UPDATE images SET sketchUrl = :sketchUrl, filled = :filledIds, isComplete = :isAllFilled WHERE id = :id")
    suspend fun updateSketchProgress(id: Int, sketchUrl: String, filledIds: List<Int>, isAllFilled: Boolean)

    // Retrieve all unique labels
    @Query("SELECT * FROM images WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<ImageData.Image>>

    @Query("UPDATE images SET filled = '[]', isComplete = 0, sketchUrl = '' WHERE id = :id")
    suspend fun resetSketchProgress(id: Int)


}