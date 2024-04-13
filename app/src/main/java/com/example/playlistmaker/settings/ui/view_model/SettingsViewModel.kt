package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {


    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = App()
                val sharingInteractor = Creator.provideSharingInteractor()
                val settingsInteractor = Creator.provideSettingsInteractor(app)
                SettingsViewModel(
                    sharingInteractor,
                    settingsInteractor
                )
            }
        }
    }

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