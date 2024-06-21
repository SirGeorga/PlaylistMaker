package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavouriteTrackInteractor
import com.example.playlistmaker.library.ui.model.FavouriteState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favoriteTrackInteractor: FavouriteTrackInteractor) : ViewModel() {
    init {
        fillData()
    }
    fun fillData() {
        viewModelScope.launch {
             favoriteTrackInteractor
                 .getFavoriteTrackList()
                 .collect{ list ->
                     processResult(list)
                 }
        }
    }

    private val favouritesLiveData = MutableLiveData<FavouriteState>()
    fun observeFavorites(): LiveData<FavouriteState> = favouritesLiveData

    private fun processResult(trackList: List<Track>){
        if(trackList.isEmpty()) renderState(FavouriteState.Empty)
        else renderState(FavouriteState.Content(trackList))
    }

    fun renderState(favoriteState: FavouriteState){
        favouritesLiveData.postValue(favoriteState)
    }
}