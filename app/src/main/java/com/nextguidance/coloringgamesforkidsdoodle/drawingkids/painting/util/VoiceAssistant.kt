package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class VoiceAssistant @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tinyDB: TinyDB
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false
    private val pendingSpeechQueue = mutableListOf<String>()

    private val successPhrases = listOf(
        "Great job!", "Amazing!", "Well done!", "Fantastic!",
        "You did it!", "Awesome!", "Excellent!", "Super job!",
        "Wonderful!", "That's amazing!", "Brilliant!", "Perfect!",
        "You got it!", "Nice work!", "Keep it up!", "You're doing great!",
        "Fantastic work!", "Wow! Great job!", "Super!", "Yay! You did it!",
        "That's correct!", "Amazing work!", "You're so clever!",
        "Wonderful job!", "You are doing awesome!", "Perfect match!",
        "Great thinking!", "Excellent work!", "You're a star!", "Fantastic! Keep going!"
    )

    private val errorPhrases = listOf(
        "Oops! Try again.", "Not quite!", "Almost there!",
        "Give it another go!", "Try one more time!", "Oops! Let's try again.",
        "Nice try!", "Keep trying!", "Almost!", "Try again!",
        "You can do it!", "Have another try!", "Let's try that again!",
        "Not this one!", "Keep going!", "So close!", "Almost got it!",
        "Try a different one!", "Good try!", "Let's find the right one!",
        "Don't give up!", "One more try!", "You are getting closer!", "Let's try again!"
    )

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            isReady = false
            return
        }

        val speechResult = tts?.setLanguage(Locale.US)
        if (speechResult == TextToSpeech.LANG_MISSING_DATA || speechResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            isReady = false
            return
        }

        configureVoice()
        configureSpeech()

        isReady = true

        // Flush any speech requested while initialization was ongoing
        synchronized(pendingSpeechQueue) {
            for (text in pendingSpeechQueue) {
                speakDirect(text)
            }
            pendingSpeechQueue.clear()
        }
    }

    private fun configureVoice() {
        val voices = tts?.voices ?: return

        val femaleVoice = voices.firstOrNull { voice ->
            voice.locale.language == Locale.US.language &&
                    voice.name.contains("female", ignoreCase = true)
        }

        if (femaleVoice != null) {
            tts?.voice = femaleVoice
            return
        }

        val usVoice = voices.firstOrNull { voice ->
            voice.locale == Locale.US
        }

        usVoice?.let {
            tts?.voice = it
        }
    }

    private fun configureSpeech() {
        tts?.apply {
            setPitch(1.25f)
            setSpeechRate(0.9f)
        }
    }

    private fun isSoundEnabled(): Boolean {
        return tinyDB.getBoolean(Constant.KEY_SOUND_ENABLED, true)
    }

    private fun speak(text: String) {
        if (!isSoundEnabled()) return

        if (isReady) {
            speakDirect(text)
        } else {
            synchronized(pendingSpeechQueue) {
                pendingSpeechQueue.add(text)
            }
        }
    }

    private fun speakDirect(text: String) {
        try {
            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "voice_assistant"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun speakSuccess() {
        speak(successPhrases.random())
    }

    fun speakError() {
        speak(errorPhrases.random())
    }

    fun speakLetter(letter: String) {
        speak("Letter $letter")
    }

    fun speakNumber(number: String) {
        speak("Number $number")
    }

    fun shutdown() {
        isReady = false
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tts = null
    }
}

