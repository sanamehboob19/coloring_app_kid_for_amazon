package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.DialogNoInternetBinding

class NoInternetDialog : DialogFragment() {

    private lateinit var binding: DialogNoInternetBinding
    private var onSettingsClicked: (() -> Unit)? = null
    private var onRetryClicked: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_Dialog)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogNoInternetBinding.inflate(inflater, container, false)

        initClickListener()


        return binding.root
    }

    private fun initClickListener() {
//        binding.btnSetting.setOnClickListener {
//            onSettingsClicked?.invoke()
//        }

        binding.btnRetry.setOnClickListener {
            onRetryClicked?.invoke()
        }
    }

    fun setOpenSettingsListener(onSettings: () -> Unit) {
        this.onSettingsClicked = onSettings
    }

    fun setRetryListener(onRetry: () -> Unit) {
        this.onRetryClicked = onRetry
    }

    companion object {
        fun newInstance() = NoInternetDialog()
    }
}