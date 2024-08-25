package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private val mediaPlayer = MediaPlayer()
    private var trackTimeFormat = TrackTimeFormat()
    override fun preparePlayer(
        url: String,
        onPreparedCallback: () -> Unit,
        onCompletionCallback: () -> Unit
    ) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPreparedCallback()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletionCallback()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun trackTime(): String {
        return trackTimeFormat.convertTrackTimeToString(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.release()
    }
    override fun resetPlayer() {
        mediaPlayer.reset()
    }
}