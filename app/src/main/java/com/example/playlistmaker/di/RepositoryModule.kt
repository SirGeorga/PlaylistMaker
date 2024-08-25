package com.example.playlistmaker.di

import com.example.playlistmaker.library.data.FavouriteTrackRepositoryImpl
import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.domain.api.FavouriteTrackRepository
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.data.PlaylistRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigatorRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory { PlaylistDbConverter() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), androidContext(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<ExternalNavigatorRepository> {
        ExternalNavigatorRepositoryImpl(androidContext())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl()
    }

    factory { TrackDbConverter() }

    single<FavouriteTrackRepository> {
        FavouriteTrackRepositoryImpl(get(), get())
    }
}