package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util


import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject


/**
 * Simple wrapper around SharedPreferences.
 *
 * Keeps all preference read/write operations in one place
 * and makes them easier to use throughout the app.
 */
class TinyDB @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    // ─────────────────────────────────────────────
    // String
    // ─────────────────────────────────────────────

    /**
     * Saves a String value.
     */
    fun putString(
        key: String,
        value: String
    ) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    /**
     * Retrieves a String value.
     */
    fun getString(
        key: String,
        default: String = ""
    ): String {
        return sharedPreferences.getString(key, default) ?: default
    }


    // ─────────────────────────────────────────────
    // Boolean
    // ─────────────────────────────────────────────

    /**
     * Saves a Boolean value.
     */
    fun putBoolean(
        key: String,
        value: Boolean
    ) {
        sharedPreferences.edit {
            putBoolean(key, value)
        }
    }

    /**
     * Retrieves a Boolean value.
     */
    fun getBoolean(
        key: String,
        default: Boolean = false
    ): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }


    // ─────────────────────────────────────────────
    // Integer
    // ─────────────────────────────────────────────

    /**
     * Saves an Integer value.
     */
    fun putInt(
        key: String,
        value: Int
    ) {
        sharedPreferences.edit {
            putInt(key, value)
        }
    }

    /**
     * Retrieves an Integer value.
     */
    fun getInt(
        key: String,
        default: Int = 0
    ): Int {
        return sharedPreferences.getInt(key, default)
    }
}

