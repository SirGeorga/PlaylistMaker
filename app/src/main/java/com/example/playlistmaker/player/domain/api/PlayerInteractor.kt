package com.example.playlistmaker.player.domain.api


interface PlayerInteractor {
    fun preparePlayer(url: String, onPreparedCallback: () -> Unit, onCompleteCallback: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun trackTime(): String
    fun onDestroy()

}