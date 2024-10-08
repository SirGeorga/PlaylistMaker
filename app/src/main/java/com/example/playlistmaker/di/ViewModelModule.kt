package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.view_model.ChosenPlaylistViewModel
import com.example.playlistmaker.library.ui.view_model.EditPlaylistViewModel
import com.example.playlistmaker.library.ui.view_model.FavouritesViewModel
import com.example.playlistmaker.library.ui.view_model.NewPlaylistViewModel
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.view_model.TrackSearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel {
        TrackSearchViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), androidContext(), get(), get())
    }

    viewModel {
        FavouritesViewModel(get())
    }

    viewModel {
        ChosenPlaylistViewModel(get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }

}