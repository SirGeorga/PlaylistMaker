package com.example.playlistmaker.settings.data

import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(val app:App): SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return when (app.darkThemeCheck()){
            true -> ThemeSettings.DARK_THEME
            false -> ThemeSettings.LIGHT_THEME
        }
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        app.switchTheme(when (settings){
            ThemeSettings.DARK_THEME -> true
            ThemeSettings.LIGHT_THEME -> false
        })
    }
}