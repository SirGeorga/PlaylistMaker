package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.ui.model.PlaylistsGridState
import com.example.playlistmaker.search.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { list ->
                    processResult(list)
                }
        }
    }

    private val playlistsLiveData = MutableLiveData<PlaylistsGridState>()
    fun observePlaylists(): LiveData<PlaylistsGridState> = playlistsLiveData

    private fun processResult(playlistList: List<Playlist>) {
        if (playlistList.isEmpty()) renderState(PlaylistsGridState.Empty)
        else renderState(PlaylistsGridState.Content(playlistList))
    }

    fun renderState(playlistsGridState: PlaylistsGridState) {
        playlistsLiveData.postValue(playlistsGridState)
    }
}