package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util


import android.content.Context
import android.widget.Toast

object ToastUtils {
    private var currentToast: Toast? = null
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun show(message: String, isLong: Boolean = false) {
        if (!::appContext.isInitialized) return
        currentToast?.cancel()
        currentToast = Toast.makeText(appContext, message, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
        currentToast?.show()
    }
}