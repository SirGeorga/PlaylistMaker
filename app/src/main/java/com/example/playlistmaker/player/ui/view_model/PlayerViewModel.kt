package com.example.playlistmaker.player.ui.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.search.domain.model.Track

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    track: Track,
    private val context: Context
) : ViewModel() {
    private var playerStateLiveData = MutableLiveData<PlayerState>()
    private val handler = Handler(Looper.getMainLooper())

    init {
        playerInteractor.preparePlayer(
            url = track.previewUrl,
            onPreparedCallback = {
                PlayerState(
                    progress = context.getString(R.string.st_00_00),
                    isPlaying = false,
                    prepared = true,
                    completed = false
                )
            },
            onCompleteCallback = {
                playerStateLiveData.value = getCurrentPlayerState().copy(
                    progress = context.getString(R.string.st_00_00),
                    isPlaying = false,
                    prepared = true,
                    completed = true
                )
            }
        )
    }

    fun getPlayerStatusLiveData(): LiveData<PlayerState> = playerStateLiveData
    fun getCurrentPlayerState(): PlayerState {
        return playerStateLiveData.value ?: PlayerState(
            progress = context.getString(R.string.st_00_00),
            isPlaying = false,
            prepared = true,
            completed = false
        )
    }

    override fun onCleared() {
        handler.removeCallbacks(getTrackTime())
        playerInteractor.onDestroy()
    }

    private fun getTrackTime(): Runnable {
        return object : Runnable {
            override fun run() {
                if (getCurrentPlayerState().isPlaying) {
                    playerStateLiveData.value =
                        getCurrentPlayerState().copy(progress = playerInteractor.trackTime())
                    handler.postDelayed(this, DELAY)
                }
            }
        }
    }

    private fun startPlayer() {
        playerStateLiveData.value = getCurrentPlayerState().copy(isPlaying = true)
        handler.post(getTrackTime())
        playerInteractor.startPlayer()
    }

    fun pausePlayer() {
        if (getCurrentPlayerState().isPlaying) {
            playerInteractor.pausePlayer()
        }
        playerStateLiveData.value = getCurrentPlayerState().copy(isPlaying = false)
        handler.removeCallbacks(getTrackTime())
    }

    fun playbackControl() {
        if (getCurrentPlayerState().isPlaying)
            pausePlayer()
        else
            startPlayer()
    }

    companion object {
        private const val DELAY = 1000L

        fun getViewModelFactory(track: Track, context: Context): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val interactor = Creator.providePlayerInteractor()
                    PlayerViewModel(
                        interactor,
                        track,
                        context
                    )
                }
            }
    }
}