package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.repository


import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.ColoringTemplateEntity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.network.ApiService
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.db.ColoringTemplateDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class ColoringRepository @Inject constructor(
    private val apiService: ApiService,
    private val templateDao: ColoringTemplateDao
) {
    companion object {
        private const val TAG = "GitHubSyncDebug"
    }

    val allTemplates: Flow<List<ColoringTemplateEntity>> = templateDao.getAllTemplates()

    fun getTemplatesByCategory(categoryKey: String): Flow<List<ColoringTemplateEntity>> {
        Log.d(TAG, "Querying Room DB for category: $categoryKey")
        return templateDao.getTemplatesByCategory(categoryKey)
    }

    suspend fun fetchAndSyncTemplates(): Boolean {
        return try {
            Log.d(TAG, "Attempting to fetch JSON data from GitHub...")
            val response = apiService.fetchData()

            if (response.isSuccessful && response.body() != null) {
                val apiData = response.body()!!
                Log.d(TAG, "Successfully downloaded GitHub JSON! Categories found: ${apiData.images.size}")

                val allEntities = mutableListOf<ColoringTemplateEntity>()

                apiData.images.filterNotNull().forEach { imageItem ->
                    val category = imageItem.category.takeIf { it.isNotBlank() } ?: "animals"
                    val total = imageItem.total
                    Log.d(TAG, "Processing Category: '$category' with total images: $total")

                    for (i in 0 until total) {
                        // Automatically generates "1.png", "2.png", "3.png"... up to 'total' dynamically
                        val fileName = (i + 1).toString()
                        val imageUrl = convertToGitUrl("templates/$category/$fileName.png")

                        Log.d(TAG, "Generated Image URL -> $imageUrl")

                        allEntities.add(
                            ColoringTemplateEntity(
                                templateId = allEntities.size + 1,
                                title = "${category.replaceFirstChar { it.uppercase() }} Item ${i + 1}",
                                previewUrl = imageUrl,
                                category = category.lowercase(),
                                isLocked = imageItem.premium?.contains(i + 1) == true
                            )
                        )
                    }
                }

                Log.d(TAG, "Clearing old local templates and saving ${allEntities.size} new templates to Room DB.")
                templateDao.deleteAllTemplates()
                templateDao.insertTemplates(allEntities)
                true
            } else {
                Log.e(TAG, "GitHub API failed with code: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during GitHub fetch/sync: ${e.message}", e)
            false
        }
    }




    private fun convertToGitUrl(relativePath: String): String {
        val baseUrl = "https://raw.githubusercontent.com/CodeCraftX-bySana/my-coloring-game-data/main/"
        return "$baseUrl$relativePath"
    }

    suspend fun hasLocalData(): Boolean {
        val count = templateDao.getCount()
        Log.d(TAG, "Checking local Room DB item count: $count")
        return count > 0
    }
}