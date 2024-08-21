package com.example.playlistmaker.library.ui.model

import com.example.playlistmaker.search.domain.model.Playlist

sealed interface PlaylistsGridState {

    object Empty : PlaylistsGridState

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsGridState
}