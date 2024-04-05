package com.example.playlistmaker.domain.api

import android.media.MediaPlayer
import android.os.Handler


interface PlayerInteractor {
    val handler: Handler
    var mediaPlayer: MediaPlayer
    var stopWatchStarted: Boolean
    var timeElapse: Long
    fun startPlayer()
    fun pausePlayer()
    fun playbackControl()
    fun preparePlayer()
    fun startStopwatch(): Runnable
}