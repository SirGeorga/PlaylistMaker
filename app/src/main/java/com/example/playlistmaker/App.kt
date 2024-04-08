package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.search.SEARCH_HISTORY_PREFERENCES

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

        savedHistory = applicationContext.getSharedPreferences(
            SEARCH_HISTORY_PREFERENCES, Context.MODE_PRIVATE
        )

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

    fun darkThemeCheck(): Boolean{
        var isNight: Boolean
        val currentNightMode = context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        isNight = when (currentNightMode){
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        return isNight
    }

    companion object {
        lateinit var savedHistory: SharedPreferences
        fun getSharedPreferences(): SharedPreferences {
            return savedHistory
        }

        var mediaHistoryList = ArrayList<Track>()

        var context: Context? = null
    }
}