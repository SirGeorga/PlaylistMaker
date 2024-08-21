package com.example.playlistmaker.library.data.db

import com.example.playlistmaker.library.data.AppDatabase
import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
) : PlaylistRepository {


    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(track: Track, playlist: Playlist) {
        playlist.trackIdList.add(track.trackId)
        appDatabase.playlistDao().updatePlaylist(
            playlistDbConverter.map(
                playlist.copy(numberOfTracks = playlist.numberOfTracks + 1)
            )
        )
        addTrackInPlaylistToDb(track)

    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>?): List<Playlist> {
        return playlists?.map { playlist -> playlistDbConverter.map(playlist) } ?: emptyList()
    }

    private suspend fun addTrackInPlaylistToDb(track: Track) {
        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(playlistDbConverter.map(track))
    }
}