package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository):
    SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        settingsRepository.updateThemeSettings(settings)
    }


}