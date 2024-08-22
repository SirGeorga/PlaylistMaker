package com.example.playlistmaker.library.domain.impl

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.updateTrackListInPlaylist(track, playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override fun sharePlaylist(message: String, context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        val chooserIntent = Intent.createChooser(shareIntent, null)
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooserIntent)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override fun getTracksFromPlaylist(trackIdList: List<Int>): Flow<List<Track>> {
        return playlistRepository.getTracksFromPlaylist(trackIdList)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist) {
        playlistRepository.deleteTrackFromPlaylist(trackId, playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }


}