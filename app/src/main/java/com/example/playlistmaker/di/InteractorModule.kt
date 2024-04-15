package com.example.playlistmaker.di

import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            get(),
            androidContext().getString(R.string.st_practicum_link),
            androidContext().getString(R.string.st_dev_email),
            androidContext().getString(R.string.st_mail_text),
            androidContext().getString(R.string.st_mail_subject),
            androidContext().getString(R.string.st_agreement_link)
        )
    }
    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }
}