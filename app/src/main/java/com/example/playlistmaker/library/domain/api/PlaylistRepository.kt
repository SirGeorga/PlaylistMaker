package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(track: Track, playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>
}