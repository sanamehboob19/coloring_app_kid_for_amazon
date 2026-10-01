package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.repository

import android.util.Log
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db.CollectionDao
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.db.ImageDao
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ImageData
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.util.PreferencesManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.network.ApiService
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepository @Inject constructor(

    private val apiService: ApiService,
    private val imageDao: ImageDao,
    private val collectionDao: CollectionDao,
)
{

    suspend fun getDbCount(): Int = imageDao.getCount()

    suspend fun fetchAndSaveImages() {
        try {
            Log.d("REPO_DEBUG", "Step 1: Fetching data from internet...")
            val response = apiService.fetchColorByNumberData()
            if (response.isSuccessful) {
                val apiData = response.body()
                Log.d("REPO_DEBUG", "Step 2: API Success. Version: ${apiData?.dataVersion}")
                Log.d("REPO_DEBUG", "Step 3: Total categories found: ${apiData?.images?.size}")

                val allImages = mutableListOf<ImageData.Image>()

                // Alphabet mapping matching your a.png, b.png, c.png files in Orignal/Sketch/Svg folders
                val letterList = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")

                apiData?.images?.filterNotNull()?.forEach { imageItem ->
                    val category = imageItem.category.takeIf { it.isNotBlank() } ?: "Unknown"

                    // Skip if it's not a color-by-number folder category
                    if (category.lowercase() == "magic" || category.lowercase() == "templates") return@forEach

                    val totalImages = imageItem.total
                    Log.d("REPO_DEBUG", "Processing category: $category | Total: $totalImages")

                    for (i in 0 until totalImages) {
                        val fileName = letterList.getOrElse(i) { "a" }
                        val image = ImageData.Image(
                            originalUrl = convertToGitUrl("Orignal/$category/$fileName.png"),
                            sketchUrl = convertToGitUrl("Sketch/$category/$fileName.jpg"),
                            svgUrl = convertToGitUrl("Svg/$category/$fileName.svg"),
                            category = category,
                            label = "",
                            premium = imageItem.premium?.contains(i + 1) ?: false,
                            addedTime = System.currentTimeMillis()
                        )
                        allImages.add(image)
                    }
                }

                Log.d("REPO_DEBUG", "Step 4: Total images created: ${allImages.size}")

                val apiVersion = apiData?.dataVersion ?: 0
                val localVersion = PreferencesManager.getInt(Constant.KEY_DATA_VERSION)

                if (localVersion < apiVersion || imageDao.getCount() == 0) {
                    imageDao.clearImages()
                    imageDao.insertImages(allImages)
                    PreferencesManager.putInt(Constant.KEY_DATA_VERSION, apiVersion)
                }
                Log.d("REPO_DEBUG", "Step 5: Saved to Database successfully!")
            }
        } catch (e: Exception) {
            Log.e("REPO_DEBUG", "Fetch failed: ${e.message}")
        }
    }




    private fun convertToGitUrl(relativePath: String): String {
//        val baseUrl = "https://raw.githubusercontent.com/Tatto112/color-by-number-game/main/"
        val baseUrl = "https://raw.githubusercontent.com/CodeCraftX-bySana/my-coloring-game-data/main/"
        return "$baseUrl$relativePath"
    }


    fun getImages(): Flow<List<ImageData.Image>> = imageDao.getAllImages()

    suspend fun updateSketchProgress(id: Int, sketchUrl: String, filledIds: List<Int>, isAllFilled: Boolean) =
        imageDao.updateSketchProgress(id, sketchUrl, filledIds, isAllFilled)


    suspend fun resetProgress(id: Int) = imageDao.resetSketchProgress(id)




}