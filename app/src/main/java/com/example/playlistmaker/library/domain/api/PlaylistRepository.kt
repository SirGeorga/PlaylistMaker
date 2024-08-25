package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun deletePlaylist(playlist: Playlist)

    fun getPlaylistById(playlistId: Int): Flow<Playlist>

    fun getTracksFromPlaylist(trackIdList: List<Int>): Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist)

    suspend fun updateTrackListInPlaylist(track: Track, playlist: Playlist)
}