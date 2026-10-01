package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.app


import android.app.Application
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.util.PreferencesManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.VoiceAssistant
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var voiceAssistant: VoiceAssistant


    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        PreferencesManager.init(this)

    }
}