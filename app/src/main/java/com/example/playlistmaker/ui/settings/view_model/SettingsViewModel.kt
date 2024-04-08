package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.ThemeSettings
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel (
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
    ) : ViewModel() {


    companion object{
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

    fun shareApp(){
        sharingInteractor.shareApp()
    }

    fun openTerms(){
        sharingInteractor.openTerms()
    }

    fun openSupport(){
        sharingInteractor.openSupport()
    }

    fun getTheme(): Boolean{
        return when(settingsInteractor.getThemeSettings()){
            ThemeSettings.DARK_THEME -> true
            ThemeSettings.LIGHT_THEME -> false
        }
    }

    fun changeTheme(checked: Boolean){
        if(checked) settingsInteractor.updateThemeSettings(ThemeSettings.DARK_THEME)
        else settingsInteractor.updateThemeSettings(ThemeSettings.LIGHT_THEME)
    }
}