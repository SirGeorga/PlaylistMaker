package com.example.playlistmaker.data.settings.impl

import com.example.playlistmaker.App
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.models.ThemeSettings

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