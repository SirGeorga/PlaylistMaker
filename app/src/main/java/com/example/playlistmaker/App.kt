package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.markodevcic.peko.PermissionRequester
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private val THEME_PREFS = "theme_preferences"
    private val THEME_PREF_KEY = "theme_key"
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        instance = applicationContext
        PermissionRequester.initialize(instance)
        sharedPrefs = getSharedPreferences(THEME_PREFS, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_PREF_KEY, darkThemeCheck())
        switchTheme(darkTheme)

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
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
            instance.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        isNight = when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        return isNight
    }

    companion object {
        lateinit var instance: Context
        lateinit var sharedPrefs: SharedPreferences
        val scope = MainScope()
    }
}