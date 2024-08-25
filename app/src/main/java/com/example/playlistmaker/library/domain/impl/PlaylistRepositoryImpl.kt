package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.data.AppDatabase
import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.TrackInPlaylistEntity
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

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        emit(playlistDbConverter.map(playlist))
    }

    override fun getTracksFromPlaylist(trackIdList: List<Int>): Flow<List<Track>> = flow {
        val resultTrackList = mutableListOf<TrackInPlaylistEntity>()
        val trackListFromDb = appDatabase.trackInPlaylistDao().getTracks()
        trackListFromDb?.forEach {
            if (trackIdList.contains(it.trackId)) resultTrackList.add(it)
        }
        emit(convertFromTrackInPlaylistEntity(resultTrackList))
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist) {
        playlist.trackIdList.remove(trackId)
        appDatabase.playlistDao().updatePlaylist(
            playlistDbConverter.map(
                playlist.copy(numberOfTracks = playlist.numberOfTracks - 1)
            )
        )
        deleteTrackInPlaylistFromDb(trackId)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>?): List<Playlist> {
        return playlists?.map { playlist -> playlistDbConverter.map(playlist) } ?: emptyList()
    }

    private fun convertFromTrackInPlaylistEntity(
        tracks: List<TrackInPlaylistEntity>?
    ): List<Track> {
        return tracks?.map { track ->
            playlistDbConverter.map(track)
        } ?: emptyList()
    }

    private suspend fun addTrackInPlaylistToDb(track: Track) {
        appDatabase.trackInPlaylistDao().insertTrackInPlaylist(playlistDbConverter.map(track))
    }

    override suspend fun updateTrackListInPlaylist(track: Track, playlist: Playlist) {
        playlist.trackIdList.add(track.trackId)
        appDatabase.playlistDao().updatePlaylist(
            playlistDbConverter.map(
                playlist.copy(numberOfTracks = playlist.numberOfTracks + 1)
            )
        )
        addTrackInPlaylistToDb(track)
    }

    private suspend fun deleteTrackInPlaylistFromDb(trackId: Int) {
        val trackListFromDb = appDatabase.playlistDao().getTracksIdLists() ?: emptyList()
        val track = appDatabase.trackInPlaylistDao().getTrackById(trackId)
        val hasNoTrackInPlaylists =
            playlistDbConverter.map(trackListFromDb).none { it.contains(trackId) }
        if (hasNoTrackInPlaylists) appDatabase.trackInPlaylistDao().deleteTrackFromPlaylist(track)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlistDbConverter.map(playlist))
        playlist.trackIdList.forEach {
            deleteTrackInPlaylistFromDb(it)
        }
    }
}