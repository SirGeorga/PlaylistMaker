package com.example.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerTrackName: TextView
    private lateinit var playerArtistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var cover: ImageView
    private lateinit var backButton: TextView
    private lateinit var playButton: ImageButton
    private lateinit var elapsedTime: TextView

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var url = ""
    private var timeElapse = 0L
    private var stopWatchStarted = false
    private var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        handler = Handler(Looper.getMainLooper())
        initViews()
        initListeners()
        parseTrack()
        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun initViews() {
        playerTrackName = findViewById(R.id.playerTrackName)
        playerArtistName = findViewById(R.id.playerArtistName)
        trackTime = findViewById(R.id.time)
        album = findViewById(R.id.album)
        year = findViewById(R.id.year)
        genre = findViewById(R.id.genre)
        country = findViewById(R.id.country)
        cover = findViewById(R.id.trackCover)
        backButton = findViewById(R.id.backArrow4)
        playButton = findViewById(R.id.playButton)
        elapsedTime = findViewById(R.id.tvElapsedTime)
    }

    private fun initListeners() {
        backButton.setOnClickListener {
            finish()
        }
        playButton.setOnClickListener {
            playButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            playbackControl()
            if (!stopWatchStarted && timeElapse <= 0L) {
                handler?.removeCallbacksAndMessages(null)
                stopWatchStarted = true
                handler?.post(startStopwatch())
            }
        }
    }

    private fun parseTrack() {
        val track = intent.getParcelableExtra<Track>("track")
        playerTrackName.text = track?.trackName ?: getString(R.string.st_unknown_track)
        playerArtistName.text = track?.artistName ?: getString(R.string.st_unknown_artist)
        trackTime.text = track?.trackTimeNormal ?: getString(R.string.st_00_00)
        album.text = track?.collectionName ?: getString(R.string.st_unknown_album)
        year.text = track?.year ?: getString(R.string.st_unknown_year).take(4)
        genre.text = track?.primaryGenreName ?: getString(R.string.st_unknown_genre)
        country.text = track?.country ?: getString(R.string.st_unknown_country)
        url = track?.previewUrl ?: ""
        val getImage = track?.artworkUrl512 ?: getString(R.string.st_unknown_cover_url)
        Glide.with(this).load(getImage).placeholder(R.drawable.ic_album_placeholder).into(cover)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.ic_bt_play)
            playerState = STATE_PREPARED
            timeElapse = 0L
            elapsedTime.text =
                String.format("%02d:%02d", timeElapse / (DELAY * 60), timeElapse / DELAY)
            stopWatchStarted = false
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_bt_pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_bt_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startStopwatch(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    elapsedTime.text =
                        String.format("%02d:%02d", timeElapse / (DELAY * 60), timeElapse / DELAY)
                    timeElapse += DELAY
                }
                handler?.postDelayed(this, DELAY)
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