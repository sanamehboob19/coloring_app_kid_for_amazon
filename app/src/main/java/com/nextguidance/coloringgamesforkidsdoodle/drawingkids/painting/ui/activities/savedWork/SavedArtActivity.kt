package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.savedWork

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivitySavedArtBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter.SavedArtAdapter
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File



@AndroidEntryPoint
class SavedArtActivity : BaseActivity<ActivitySavedArtBinding>(
    ActivitySavedArtBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTopBar()
        loadSavedArtworks()
    }

    private fun setupTopBar() {
        binding.btnClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadSavedArtworks() {
        val bitmaps = mutableListOf<Bitmap>()
        val filePaths = mutableListOf<String>()

        try {
            // Load from shared Pictures/KidsColoring directory
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KidsColoring")
            if (dir.exists()) {
                val files = dir.listFiles()
                files?.sortedByDescending { it.lastModified() }?.forEach { file ->
                    if (file.absolutePath.endsWith(".png")) {
                        val bmp = BitmapFactory.decodeFile(file.absolutePath)
                        if (bmp != null) {
                            bitmaps.add(bmp)
                            filePaths.add(file.absolutePath)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (bitmaps.isEmpty()) {
            binding.rvSavedArt.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvSavedArt.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE

            binding.rvSavedArt.layoutManager = GridLayoutManager(this, 2)
            binding.rvSavedArt.adapter = SavedArtAdapter(bitmaps) { position ->
                val clickedPath = filePaths[position]
                val intent = Intent(this, SavedArtDetailActivity::class.java).apply {
                    putExtra(SavedArtDetailActivity.EXTRA_FILE_PATH, clickedPath)
                }
                startActivityForResult(intent, 100)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Reload list after an artwork is deleted
            loadSavedArtworks()
        }
    }
}