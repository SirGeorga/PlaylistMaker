package com.example.playlistmaker.player.ui.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.api.FavouriteTrackInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val context: Context,
    private val favouriteTrackInteractor: FavouriteTrackInteractor,
) : ViewModel() {
    private var playerStateLiveData = MutableLiveData<PlayerState>()
    private var timerJob: Job? = null
    fun preparePlayerVM(track: Track) {
        playerInteractor.resetPlayer()
        playerInteractor.preparePlayer(url = track.previewUrl, onPreparedCallback = {
            PlayerState(
                progress = context.getString(R.string.st_00_00),
                isPlaying = false,
                prepared = true,
                completed = false,
                isFavourite = track.isFavourite
            )
        }, onCompleteCallback = {
            playerStateLiveData.value = getCurrentPlayerState().copy(
                progress = context.getString(R.string.st_00_00),
                isPlaying = false,
                prepared = true,
                completed = true,
                isFavourite = track.isFavourite
            )
        })
    }

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState(
            progress = context.getString(R.string.st_00_00),
            isPlaying = false,
            prepared = true,
            completed = false,
            isFavourite = false
        )
    }

    override fun onCleared() {
        playerInteractor.onDestroy()
    }

    private fun getTrackTime() {
        timerJob = viewModelScope.launch {
            while (getCurrentPlayerState().isPlaying) {
                playerStateLiveData.value =
                    getCurrentPlayerState().copy(progress = playerInteractor.trackTime())
                delay(DELAY)
            }
        }
    }

    private fun startPlayer() {
        playerStateLiveData.value = getCurrentPlayerState().copy(isPlaying = true)
        playerInteractor.startPlayer()
        getTrackTime()
    }

    fun pausePlayer() {
        if (getCurrentPlayerState().isPlaying) {
            playerInteractor.pausePlayer()
        }
        playerStateLiveData.value = getCurrentPlayerState().copy(isPlaying = false)
        timerJob?.cancel()
    }


    fun playbackControl() {
        if (getCurrentPlayerState().isPlaying) pausePlayer()
        else startPlayer()
    }

    fun onFavouriteClicked(track: Track): Track {
        return if (track.isFavourite) deleteFromFavorite(track)
        else addToFavorite(track)
    }

    fun addToFavorite(track: Track): Track {
        viewModelScope.launch {
            favouriteTrackInteractor.addTrackToFavorite(track)
            playerStateLiveData.value = getCurrentPlayerState().copy(isFavourite = true)
        }
        return track.copy(isFavourite = true)
    }
    fun deleteFromFavorite(track: Track): Track {
        viewModelScope.launch {
            favouriteTrackInteractor.deleteTrackFromFavorites(track)
            playerStateLiveData.value = getCurrentPlayerState().copy(isFavourite = false)
        }
        return track.copy(isFavourite = false)
    }

    companion object {
        private const val DELAY = 300L
    }
}