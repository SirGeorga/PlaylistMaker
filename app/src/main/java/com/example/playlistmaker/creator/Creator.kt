package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.impl.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"
    private lateinit var app: App

    fun initialize(app: App) {
        this.app = app
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(app),
            SearchHistory(
                app.getSharedPreferences(
                    SEARCH_HISTORY_PREFERENCES,
                    Context.MODE_PRIVATE
                )
            ),
            app.applicationContext
        )
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigator(),
            app.resources.getString(R.string.st_practicum_link),
            app.resources.getString(R.string.st_dev_email),
            app.resources.getString(R.string.st_mail_text),
            app.resources.getString(R.string.st_mail_subject),
            app.resources.getString(R.string.st_agreement_link)
        )
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(app.applicationContext)
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(app)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}