package com.example.playlistmaker.data.settings

import com.example.playlistmaker.domain.models.ThemeSettings

interface SettingsRepository {
    fun  getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}