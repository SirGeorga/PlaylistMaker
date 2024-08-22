package com.example.playlistmaker.library.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.App.Companion.scope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ChosenPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    var currentTrack: Track? = null
    var currentPlaylist = Playlist()
    var currentTrackList = mutableListOf<Track>()

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                processResultPlaylist(playlist)
            }
        }
    }

    fun getTracksFromPlaylist(trackIdList: List<Int>) {
        viewModelScope.launch {
            playlistInteractor.getTracksFromPlaylist(trackIdList).collect { trackList ->
                processResultTrackList(trackList)
            }
        }
    }

    fun deletePlaylist() {
        scope.launch {
            playlistInteractor.deletePlaylist(currentPlaylist)
        }
        playlistLiveData.postValue(null)
    }

    private val playlistLiveData = MutableLiveData<Playlist?>()
    fun observeChosenPlaylist(): LiveData<Playlist?> = playlistLiveData

    private val trackListLiveData = MutableLiveData<List<Track>>()
    fun observeTrackList(): LiveData<List<Track>> = trackListLiveData

    private fun processResultPlaylist(playlist: Playlist) {
        currentPlaylist = playlist
        getTracksFromPlaylist(playlist.trackIdList)
        playlistLiveData.postValue(playlist)
    }

    private fun processResultTrackList(trackList: List<Track>) {
        currentTrackList.clear()
        currentTrackList.addAll(trackList)
        trackListLiveData.postValue(trackList)
    }

    fun deleteTrackFromPlaylist() {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(currentTrack!!.trackId, currentPlaylist)
            getPlaylistById(currentPlaylist.playlistId)
        }
    }

    fun sharePlaylist(context: Context) {
        val message = makeMessage(currentPlaylist, currentTrackList, context)
        playlistInteractor.sharePlaylist(message, context)
    }

    private fun makeMessage(playlist: Playlist, trackList: List<Track>, context: Context): String {
        return "${playlist.playlistName}\n" + "${playlist.playlistDescription}\n" + "${
            context.resources.getQuantityString(
                R.plurals.plurals_tracks_add, playlist.numberOfTracks, playlist.numberOfTracks
            )
        }\n" + trackList.joinToString(separator = "\n") {
            "${trackList.indexOf(it) + 1}" + ".${it.artistName} " + "- ${it.trackName}(${
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis.toLong())
            })"
        }
    }
}