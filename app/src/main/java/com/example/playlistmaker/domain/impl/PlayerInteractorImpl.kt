package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val playButton: ImageButton,
    private val url: String,
    private val tvElapsedTime: TextView
) : PlayerInteractor {
    private var playerState = STATE_DEFAULT
    override var mediaPlayer = MediaPlayer()
    override var stopWatchStarted = false
    override var timeElapse = 0L
    override val handler = Handler(Looper.getMainLooper())
    override fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_bt_pause)
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_bt_play)
        playerState = STATE_PAUSED
    }

    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_bt_play)
            playerState = STATE_PREPARED
            timeElapse = 0L
            tvElapsedTime.text =
                String.format("%02d:%02d", timeElapse / (DELAY * 60), timeElapse / DELAY)
            stopWatchStarted = false
        }
    }

    override fun startStopwatch(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    tvElapsedTime.text =
                        String.format("%02d:%02d", timeElapse / (DELAY * 60), timeElapse / DELAY)
                    timeElapse += DELAY
                }
                handler.postDelayed(this, DELAY)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }
}