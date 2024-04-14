package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App : Application() {
    private val THEME_PREFS = "theme_preferences"
    private val THEME_PREF_KEY = "theme_key"
    private var darkTheme = false
    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        context = this

        sharedPrefs = getSharedPreferences(THEME_PREFS, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_PREF_KEY, darkThemeCheck())
        switchTheme(darkTheme)
        Creator.initialize(this)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefs.edit().putBoolean(THEME_PREF_KEY, darkThemeEnabled).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun darkThemeCheck(): Boolean {
        val isNight: Boolean
        val currentNightMode =
            context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        isNight = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        return isNight
    }
}