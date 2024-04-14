package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track

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
    private lateinit var tvElapsedTime: TextView
    private lateinit var viewModel: PlayerViewModel
    private var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        initListeners()

        val track = intent.getParcelableExtra<Track>("track") as Track
        parseTrack(track)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(track, this)
        )[PlayerViewModel::class.java]

        viewModel.getPlayerStatusLiveData().observe(this) { playerState ->
            changePlayButton(playerState)
            tvElapsedTime.text = playerState.progress
        }
    }

    private fun changePlayButton(playerState: PlayerState) {
        if (playerState.isPlaying)
            playButton.setImageResource(R.drawable.ic_bt_pause)
        else
            playButton.setImageResource(R.drawable.ic_bt_play)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
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
        tvElapsedTime = findViewById(R.id.tvElapsedTime)
    }

    private fun initListeners() {
        backButton.setOnClickListener {
            finish()
        }
        playButton.setOnClickListener {
            playButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            viewModel.playbackControl()
        }
    }

    private fun parseTrack(track: Track) {
        playerTrackName.text = track.trackName
        playerArtistName.text = track.artistName
        trackTime.text = track.trackTimeNormal
        album.text = track.collectionName
        year.text = track.year
        genre.text = track.primaryGenreName
        country.text = track.country
        url = track.previewUrl
        val getImage = track.artworkUrl512
        Glide.with(this).load(getImage).placeholder(R.drawable.ic_album_placeholder).into(cover)
    }
}

