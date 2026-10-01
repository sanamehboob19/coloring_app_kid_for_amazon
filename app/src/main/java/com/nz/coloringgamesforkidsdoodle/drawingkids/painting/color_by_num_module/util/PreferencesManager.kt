package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson


/**
 * Singleton class to manage shared preferences in the application.
 */
object PreferencesManager {

    private lateinit var preferences: SharedPreferences

    private const val IMAGE_LIST_KEY = "image_list_key"
    private const val FAV_LIST_KEY = "fav_list_key"

    /**
     * Initializes the PreferencesManager with the application context.
     * Should be called once in the application class or main activity.
     *
     * @param context Application context
     */
    fun init(context: Context) {
        if (!::preferences.isInitialized) preferences = context.applicationContext.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE)
    }

    /**
     * Checks if the PreferencesManager has been initialized.
     *
     * @return True if initialized, false otherwise.
     */
    fun hasInstance(): Boolean {
        return this::preferences.isInitialized
    }

    /**
     * Stores a string value in shared preferences.
     *
     * @param key Key for the preference
     * @param value Value to be stored
     */
    fun putString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    /**
     * Stores a long value in shared preferences.
     *
     * @param key Key for the preference
     * @param value Value to be stored
     */
    fun putLong(key: String, value: Long) {
        preferences.edit { putLong(key, value) }
    }

    /**
     * Stores an integer value in shared preferences.
     *
     * @param key Key for the preference
     * @param value Value to be stored
     */
    fun putInt(key: String, value: Int) {
        preferences.edit { putInt(key, value) }
    }

    /**
     * Stores a boolean value in shared preferences.
     *
     * @param key Key for the preference
     * @param value Value to be stored
     */
    fun putBoolean(key: String, value: Boolean) {
        preferences.edit { putBoolean(key, value) }
    }

    /**
     * Retrieves a string value from shared preferences.
     *
     * @param key Key for the preference
     * @param defValue Default value to return if the key does not exist
     * @return The stored string value or the default value
     */
    fun getString(key: String, defValue: String = ""): String {
        return preferences.getString(key, defValue) ?: defValue
    }

    /**
     * Retrieves a long value from shared preferences.
     *
     * @param key Key for the preference
     * @param defValue Default value to return if the key does not exist
     * @return The stored long value or the default value
     */
    fun getLong(key: String, defValue: Long = 0L): Long {
        return preferences.getLong(key, defValue)
    }

    /**
     * Retrieves an integer value from shared preferences.
     *
     * @param key Key for the preference
     * @param defValue Default value to return if the key does not exist
     * @return The stored integer value or the default value
     */
    fun getInt(key: String, defValue: Int = 0): Int {
        return preferences.getInt(key, defValue)
    }

    /**
     * Retrieves a boolean value from shared preferences.
     *
     * @param key Key for the preference
     * @param defValue Default value to return if the key does not exist
     * @return The stored boolean value or the default value
     */
    fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    /**
     * Checks if the shared preferences contains a specific key.
     *
     * @param key Key to check
     * @return True if the key exists, false otherwise
     */
    fun contains(key: String): Boolean {
        return preferences.contains(key)
    }

    /**
     * Removes a specific key from shared preferences.
     *
     * @param key Key to be removed
     */
    fun remove(key: String) {
        preferences.edit { remove(key) }
    }

    /**
     * Clears all data from shared preferences.
     */
    fun clear() {
        preferences.edit { clear() }
    }

    /**
     * Stores a list of image paths in shared preferences.
     *
     * @param imageList List of image paths to store
     */
    fun putImageList(imageList: List<String>) {
        val json = Gson().toJson(imageList)
        putString(IMAGE_LIST_KEY, json)
    }

    /**
     * Retrieves the list of image paths from shared preferences.
     *
     * @return List of image paths or an empty list if none exist
     */
    fun getImageList(): List<String> {
        val json = getString(IMAGE_LIST_KEY, "")
        return if (json.isNotEmpty()) {
            Gson().fromJson(json, Array<String>::class.java).toList()  // Use Array<String> instead of TypeToken
        } else {
            emptyList()
        }
    }

    /**
     * Adds a new image path to the existing list at a specific index.
     *
     * @param index Index to insert the new image path
     * @param imagePath Path of the image to add
     */
    fun addImageAtIndex(index: Int, imagePath: String) {
        val currentList = getImageList().toMutableList()
        if (index in 0..currentList.size) {
            currentList.add(index, imagePath)
            putImageList(currentList)
        }
    }

    fun putFavList(favList: List<Int>) {
        val json = Gson().toJson(favList)
        putString(FAV_LIST_KEY, json)
    }


    fun getFavList(): List<Int> {
        val json = getString(FAV_LIST_KEY, "")
        return if (json.isNotEmpty()) {
            Gson().fromJson(json, Array<Int>::class.java).toList()
        } else {
            emptyList()
        }
    }


    fun <T> putModel(key: String, model: T) {
        val json = Gson().toJson(model)
        preferences.edit { putString(key, json) }
    }

    // ✅ Retrieve single model
    internal inline fun <reified T> getModel(key: String): T? {
        val json = preferences.getString(key, null) ?: return null
        return Gson().fromJson(json, T::class.java)
    }

    /**
     * Clears the list of image paths from shared preferences.
     */
    fun clearImageList() {
        putImageList(emptyList())
    }

    fun isPremiumUser(): Boolean {
        return false
    }


}