package com.example.playlistmaker.library.ui.model

import com.example.playlistmaker.search.domain.model.Track

interface FavouriteState {
    object Empty : FavouriteState

    data class Content(
        val tracks: List<Track>
    ) : FavouriteState
}