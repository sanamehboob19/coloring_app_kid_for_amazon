package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.savedWork

import android.os.Bundle
import android.graphics.BitmapFactory
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ActivitySavedArtDetailBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base.BaseActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SavedArtDetailActivity : BaseActivity<ActivitySavedArtDetailBinding>(ActivitySavedArtDetailBinding::inflate) {

    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filePath = intent.getStringExtra(EXTRA_FILE_PATH)
        loadImage()
        setupListeners()
    }

    private fun loadImage() {
        if (!filePath.isNullOrEmpty()) {
            val bitmap = BitmapFactory.decodeFile(filePath)
            if (bitmap != null) {
                binding.ivFullArt.setImageBitmap(bitmap)
            } else {
                ToastUtils.show("Unable to load image")
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnDelete.setOnClickListener {
            if (!filePath.isNullOrEmpty()) {
                val file = File(filePath!!)
                if (file.exists() && file.delete()) {
                    ToastUtils.show("Artwork deleted successfully 🗑️")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    ToastUtils.show("Failed to delete artwork")
                }
            }
        }
    }

    companion object {
        const val EXTRA_FILE_PATH = "extra_file_path"
    }
}