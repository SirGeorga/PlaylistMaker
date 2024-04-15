package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun getTheme(): Boolean {
        return when (settingsInteractor.getThemeSettings()) {
            ThemeSettings.DARK_THEME -> true
            ThemeSettings.LIGHT_THEME -> false
        }
    }

    fun changeTheme(checked: Boolean) {
        if (checked) settingsInteractor.updateThemeSettings(ThemeSettings.DARK_THEME)
        else settingsInteractor.updateThemeSettings(ThemeSettings.LIGHT_THEME)
    }
}