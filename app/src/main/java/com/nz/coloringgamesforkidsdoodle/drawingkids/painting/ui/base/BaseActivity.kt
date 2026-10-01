package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.extension.setupGlobalTvFocus
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.TinyDB
import javax.inject.Inject

abstract class BaseActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : AppCompatActivity() {

    lateinit var binding: VB

    @Inject
    lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setupGlobalTvFocus()

    }

    fun setupBackButton(backButtonId: Int, onBackClick: (() -> Unit)? = null) {
        findViewById<View>(backButtonId)?.setOnClickListener {
            if (onBackClick != null) {
                onBackClick()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}