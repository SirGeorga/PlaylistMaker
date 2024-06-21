package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModel()
    private var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
        var track: Track? =
            savedInstanceState?.getParcelable("track") ?: intent.getParcelableExtra("track")

        if (track != null) {
            parseTrack(track)
            viewModel.preparePlayerVM(track)

        } else {
            finish()
        }

        binding.btFavourites.setOnClickListener{
            binding.playButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val likedTrack = track?.let { it1 -> viewModel.onFavouriteClicked(it1) }
            track = likedTrack
        }


        viewModel.getPlayerStateLiveData().observe(this) { playerState ->
            changePlayButton(playerState)
            changeLikeButtonStyle(playerState)
            binding.tvElapsedTime.text = playerState.progress
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val track = intent.getParcelableExtra<Track>("track")
        outState.putParcelable("track", track)
    }

    private fun changePlayButton(playerState: PlayerState) {
        if (playerState.isPlaying) binding.playButton.setImageResource(R.drawable.ic_bt_pause)
        else binding.playButton.setImageResource(R.drawable.ic_bt_play)
    }

    private fun changeLikeButtonStyle(playStatus: PlayerState){
        if(playStatus.isFavourite) {
            binding.btFavourites.setImageResource(R.drawable.ic_bt_liked)
        }
        else{
            binding.btFavourites.setImageResource(R.drawable.ic_bt_like)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun initListeners() {
        binding.backArrow.setOnClickListener {
            finish()
        }
        binding.playButton.setOnClickListener {
            binding.playButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            viewModel.playbackControl()
        }
    }

    private fun parseTrack(track: Track) {
        binding.playerTrackName.text = track.trackName
        binding.playerArtistName.text = track.artistName
        binding.time.text = track.trackTimeNormal
        binding.album.text = track.collectionName
        binding.year.text = track.year
        binding.genre.text = track.primaryGenreName
        binding.country.text = track.country
        url = track.previewUrl
        val getImage = track.artworkUrl512
        Glide.with(this).load(getImage).placeholder(R.drawable.ic_album_placeholder)
            .into(binding.trackCover)
    }
}

