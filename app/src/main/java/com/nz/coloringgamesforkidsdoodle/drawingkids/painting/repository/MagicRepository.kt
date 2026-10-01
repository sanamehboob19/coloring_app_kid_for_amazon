package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.repository


import android.util.Log
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.MagicTemplateEntity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.network.ApiService
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.db.MagicTemplateDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class MagicRepository @Inject constructor(
    private val apiService: ApiService,
    private val magicDao: MagicTemplateDao
) {
    companion object {
        private const val TAG = "MagicRepoDebug"
    }

    val allMagicTemplates: Flow<List<MagicTemplateEntity>> = magicDao.getAllMagicTemplates()

    suspend fun fetchAndSyncMagicTemplates(): Boolean {
        return try {
            val response = apiService.fetchData()
            if (response.isSuccessful && response.body() != null) {
                val apiData = response.body()!!
                val allEntities = mutableListOf<MagicTemplateEntity>()


                apiData.images.filterNotNull().forEach { item ->
                    if (item.category.lowercase() == "magic") {
                        val total = item.total
                        for (i in 0 until total) {
                            val fileName = (i + 1).toString()

                            // By default use .png, but you can also customize if needed.
                            // Note: Glide will successfully load these URLs.
                            val imageUrl = "https://raw.githubusercontent.com/CodeCraftX-bySana/my-coloring-game-data/main/magic/$fileName.png"

                            allEntities.add(
                                MagicTemplateEntity(
                                    templateId = i + 1,
                                    title = "Magic Art ${i + 1}",
                                    previewUrl = imageUrl,
                                    category = "magic"
                                )
                            )
                        }
                    }
                }

                if (allEntities.isNotEmpty()) {
                    magicDao.deleteAllMagicTemplates()
                    magicDao.insertMagicTemplates(allEntities)
                    Log.d(TAG, "Magic templates successfully saved: ${allEntities.size}")
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching magic templates: ${e.message}", e)
            false
        }
    }

    suspend fun hasLocalData(): Boolean {
        return magicDao.getCount() > 0
    }
}