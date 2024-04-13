package com.example.playlistmaker.player.data.api

interface PlayerRepository {
    fun preparePlayer(url: String, onPreparedCallback: () -> Unit, onCompletionCallback: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun trackTime(): String
    fun onDestroy()
}