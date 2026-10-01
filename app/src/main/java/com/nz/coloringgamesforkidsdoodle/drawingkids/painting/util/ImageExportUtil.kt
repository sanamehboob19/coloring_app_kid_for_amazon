package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageExportUtil {

    /**
     * Saves drawing directly into device Gallery/Photos.
     */
    fun saveToGallery(context: Context, bitmap: Bitmap): Boolean {
        val filename = "KidsArt_${System.currentTimeMillis()}.png"
        var outputStream: OutputStream? = null

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/KidsColoring")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false
                outputStream = resolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
                true
            } else {
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KidsColoring")
                if (!dir.exists()) dir.mkdirs()
                val imageFile = File(dir, filename)
                outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            outputStream?.close()
        }
    }

    /**
     * Shares drawing via WhatsApp, Messages, etc.
     */
    fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "shared_art")
            cachePath.mkdirs()
            val file = File(cachePath, "kids_drawing.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Drawing"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}