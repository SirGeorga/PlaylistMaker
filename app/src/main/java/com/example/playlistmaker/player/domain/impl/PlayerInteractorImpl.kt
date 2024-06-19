package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(
        url: String,
        onPreparedCallback: () -> Unit,
        onCompleteCallback: () -> Unit
    ) {
        return repository.preparePlayer(url, onPreparedCallback, onCompleteCallback)
    }

    override fun startPlayer() {
        return repository.startPlayer()
    }

    override fun pausePlayer() {
        return repository.pausePlayer()
    }

    override fun trackTime(): String {
        return repository.trackTime()
    }

    override fun onDestroy() {
        return repository.release()
    }

    override fun resetPlayer() {
        return repository.resetPlayer()
    }

}