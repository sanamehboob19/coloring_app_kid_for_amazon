package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util


import android.content.Context
import android.content.Intent

object ShareUtil {
    fun shareApp(context: Context) {
        val appName = context.applicationInfo.loadLabel(context.packageManager).toString()
        val packageName = context.packageName
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, appName)
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this amazing coloring and drawing game for kids on Google Play! https://play.google.com/store/apps/details?id=$packageName"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}