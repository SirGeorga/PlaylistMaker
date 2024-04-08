package com.example.playlistmaker.domain.models

import android.content.Context
import android.content.res.Configuration

object ThemeManager {
    private const val PREFS = "my_prefs"
    private const val THEME_PREF = "isDarkTheme"
    fun isDarkTheme(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return sharedPref.getBoolean(THEME_PREF, currentNightMode == Configuration.UI_MODE_NIGHT_YES)
    }

}