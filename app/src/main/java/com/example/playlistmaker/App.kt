package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    val THEME_PREFS = "theme_preferences"
    val THEME_PREF_KEY = "theme_key"
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        context = this

        val sharedPrefs = getSharedPreferences(THEME_PREFS, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_PREF_KEY, darkThemeCheck())
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
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
            context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        isNight = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        return isNight
    }

    companion object {
        var context: Context? = null
    }
}