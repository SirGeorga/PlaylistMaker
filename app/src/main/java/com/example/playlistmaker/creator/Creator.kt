package com.example.playlistmaker.creator

import com.example.playlistmaker.App
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.ExternalNavigator
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.data.settings.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor() : TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl()
    }

    fun provideSharingInteractor(): SharingInteractor{
        return SharingInteractorImpl(getExternalNavigator())
    }

    private fun getSettingsRepository(app: App): SettingsRepository{
        return SettingsRepositoryImpl(app)
    }

    fun provideSettingsInteractor(app: App): SettingsInteractor{
        return SettingsInteractorImpl(getSettingsRepository(app))
    }
/*
    private fun getPlayerRepository():PlayerRepository{
        return PlayerRepositoryImpl()
    }

    fun proovidePlayerInteractor():PlayerInteractor{
        return PlayerInteractorImpl(getPlayerRepository())
    }
*/
}