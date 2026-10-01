package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.Window
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.DialogSketchCompletedBinding

class SketchCompletedDialog(
    context: Context,
    private val onProceed: (() -> Unit)? = null
) : Dialog(context)
{

    private lateinit var binding: DialogSketchCompletedBinding
    private val autoHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val AUTO_DISMISS_MS = 2_000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogSketchCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(R.color.transparent)

        setCancelable(false)

        // Auto-proceed after timeout
        autoHandler.postDelayed({ proceed() }, AUTO_DISMISS_MS)
    }


    private fun proceed() {
        autoHandler.removeCallbacksAndMessages(null)
        if (!isShowing) return
        dismiss()
        onProceed?.invoke()
    }


//    override fun dismiss() {
//        autoHandler.removeCallbacksAndMessages(null)
//        super.dismiss()
//    }

    override fun dismiss() {
        // Check if the dialog is actually showing
        if (!isShowing) return
        //  Check if the activity is still alive
        val activity = context as? Activity
        if (activity != null && (activity.isFinishing || activity.isDestroyed)) {
            return
        }

        try {
            super.dismiss()
        } catch (e: Exception) {
            // This catch block prevents the "not attached to window manager" crash
            e.printStackTrace()
        }
    }



}