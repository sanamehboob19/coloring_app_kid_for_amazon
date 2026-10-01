package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util


import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var isMutedByUser: Boolean = false
    private var isTemporarilyPaused: Boolean = false
    private var isLifecycleRegistered = false

    private val handler = Handler(Looper.getMainLooper())
    private var isAppInBackground = false

    private val pauseRunnable = Runnable {
        isAppInBackground = true
        pausePlayback()
    }

    fun start(context: Context) {
        registerLifecycle(context)

        val sharedPrefs = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
        val tinyDB = TinyDB(sharedPrefs)
        isMutedByUser = !tinyDB.getBoolean(Constant.KEY_APP_MUSIC_ENABLED, true)

        if (isMutedByUser || isTemporarilyPaused) return

        if (mediaPlayer == null) {
            try {
                // Note: Make sure you copy R.raw.app_kids_bg_music from your old project raw folder!
                mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.app_kids_bg_music)?.apply {
                    isLooping = true
                    setVolume(1.0f, 1.0f)
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun registerLifecycle(context: Context) {
        if (isLifecycleRegistered) return
        val app = context.applicationContext as? Application ?: return

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                handler.removeCallbacks(pauseRunnable)
                if (isAppInBackground) {
                    isAppInBackground = false
                    if (!isMutedByUser && !isTemporarilyPaused) {
                        start(activity)
                    }
                }
            }

            override fun onActivityPaused(activity: Activity) {
                handler.postDelayed(pauseRunnable, 400)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })

        isLifecycleRegistered = true
    }

    private fun pausePlayback() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseForCurrentScreen() {
        isTemporarilyPaused = true
        pausePlayback()
    }

    fun resumeForCurrentScreen(context: Context) {
        isTemporarilyPaused = false
        if (!isMutedByUser && !isAppInBackground) {
            start(context)
        }
    }

    fun toggleMusic(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
        val tinyDB = TinyDB(sharedPrefs)

        val newState = !tinyDB.getBoolean(Constant.KEY_APP_MUSIC_ENABLED, true)
        tinyDB.putBoolean(Constant.KEY_APP_MUSIC_ENABLED, newState)
        isMutedByUser = !newState

        if (isMutedByUser) {
            pausePlayback()
        } else {
            start(context)
        }
        return newState
    }

    fun isMusicEnabled(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
        return TinyDB(sharedPrefs).getBoolean(Constant.KEY_APP_MUSIC_ENABLED, true)
    }

    fun release() {
        handler.removeCallbacks(pauseRunnable)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}