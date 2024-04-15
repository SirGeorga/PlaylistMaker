package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.model.Track

sealed interface TracksState {
    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String
    ) : TracksState

    data class Empty(
        val message: String
    ) : TracksState
}