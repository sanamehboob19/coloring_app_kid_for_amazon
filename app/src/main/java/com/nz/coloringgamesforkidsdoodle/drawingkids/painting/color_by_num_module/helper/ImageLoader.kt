package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.util.PreferencesManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume



object ImageLoader {

    suspend fun loadBitmap(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            Glide.with(context.applicationContext)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(PreferencesManager.getInt(Constant.KEY_DATA_VERSION)))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        continuation.resume(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        continuation.resume(null)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    suspend fun loadBitmap(context: Context, resId: Int): Bitmap? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            Glide.with(context.applicationContext)
                .asBitmap()
                .load(resId)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        continuation.resume(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        continuation.resume(null)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }
}