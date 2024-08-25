package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.search.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) :
    NewPlaylistViewModel(playlistInteractor) {

    override fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.updatePlaylist(playlist)
        }
        playlistLiveData.postValue(null)
    }

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                processResultPlaylist(playlist)
            }
        }
    }

    private val playlistLiveData = MutableLiveData<Playlist?>()
    fun observePlaylist(): LiveData<Playlist?> = playlistLiveData

    private fun processResultPlaylist(playlist: Playlist) {
        playlistLiveData.postValue(playlist)
    }
}
