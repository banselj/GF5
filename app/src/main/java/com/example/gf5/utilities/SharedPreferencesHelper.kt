package com.example.gf5.utilities

import android.content.Context

class SharedPreferencesHelper(context: Context) {

    private val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    // Save methods
    fun saveString(key: String, value: String) {
        sharedPref.edit().putString(key, value).apply()
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPref.edit().putBoolean(key, value).apply()
    }

    fun saveInt(key: String, value: Int) {
        sharedPref.edit().putInt(key, value).apply()
    }

    fun saveLong(key: String, value: Long) {
        sharedPref.edit().putLong(key, value).apply()
    }

    // Retrieve methods
    fun getString(key: String, defaultValue: String): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    // Clear specific key
    fun clearKey(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    // Clear all preferences
    fun clearAll() {
        sharedPref.edit().clear().apply()
    }
}
