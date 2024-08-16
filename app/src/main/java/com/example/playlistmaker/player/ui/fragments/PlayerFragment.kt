package com.example.playlistmaker.player.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.PlayerState
import com.example.playlistmaker.player.ui.recycler_view.PlaylistBottomSheetAdapter
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.fragment.SearchFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private val itemType = object : TypeToken<Track>() {}.type

    companion object {
        private const val TRACK = "track"
        fun createArgs(track: String): Bundle =
            bundleOf(
                TRACK to track
            )
    }

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit


    private val audioplayerViewModel by viewModel<PlayerViewModel>()
    private var currentTrack: Track? = null
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val playlistBottomSheetAdapter = PlaylistBottomSheetAdapter {
        onPlaylistClickDebounce(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var convertedTrack = createTrackFromJson(requireArguments().getString(TRACK))

        onPlaylistClickDebounce = debounce<Playlist>(
            SearchFragment.SEARCH_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            addToPlaylist(playlist)
        }

        binding.tvElapsedTime.text = getString(R.string.st_00_00)
        binding.recyclerViewBottomSheet.adapter = playlistBottomSheetAdapter

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        Glide.with(this)
            .load(convertedTrack.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.search_bar_corner_radius_8dp)))
            .into(binding.trackImage)

        audioplayerViewModel.preparePlayerVM(convertedTrack)
        currentTrack = convertedTrack
        binding.tvPlayerTrackName.text = convertedTrack.trackName
        binding.tvArtistName.text = convertedTrack.artistName
        binding.tvTrackTime.text = convertedTrack.trackTimeNormal
        binding.tvTrackAlbum.text = convertedTrack.collectionName
        binding.tvTrackAlbum.visibility = View.VISIBLE
        binding.tvTrackYear?.text = convertedTrack.year
        binding.tvTrackGenre.text = convertedTrack.primaryGenreName
        binding.tvTrackCountry.text = convertedTrack.country

        audioplayerViewModel.getPlayerStateLiveData().observe(viewLifecycleOwner) { playStatus ->
            changeButtonStyle(playStatus)
            changeLikeButtonStyle(playStatus)
            binding.tvElapsedTime.text = playStatus.progress
        }

        audioplayerViewModel.observePlaylists().observe(viewLifecycleOwner) {
            showContent(it)
        }
        audioplayerViewModel.observeAddedTrack().observe(viewLifecycleOwner) {
            if (!it.first) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            Toast.makeText(requireContext(), it.second, Toast.LENGTH_LONG).show()
        }

        binding.btPlayButton.setOnClickListener {
            audioplayerViewModel.playbackControl()
        }
        binding.btLikeButton.setOnClickListener {
            val changedTrack = audioplayerViewModel.onFavouriteClicked(convertedTrack)
            convertedTrack = changedTrack
        }
        binding.btPlaylistAddButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            audioplayerViewModel.fillData()
        }
        binding.newPlaylistBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun addToPlaylist(playlist: Playlist) {
        return audioplayerViewModel.addToPlaylist(currentTrack!!, playlist)
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistBottomSheetAdapter.setPlaylists(playlists)
    }

    private fun changeButtonStyle(playStatus: PlayerState) {
        if (playStatus.completed) {
            binding.btPlayButton.setImageResource(R.drawable.ic_bt_play)
            binding.tvElapsedTime.text = getString(R.string.st_00_00)
        }
        if (playStatus.isPlaying) {
            binding.btPlayButton.setImageResource(R.drawable.ic_bt_pause)
        } else {
            binding.btPlayButton.setImageResource(R.drawable.ic_bt_play)
        }
    }

    private fun changeLikeButtonStyle(playStatus: PlayerState) {
        if (playStatus.isFavourite) {
            binding.btLikeButton.setImageResource(R.drawable.ic_bt_liked)
        } else {
            binding.btLikeButton.setImageResource(R.drawable.ic_bt_like)
        }
    }

    private fun createTrackFromJson(json: String?): Track {
        return Gson().fromJson(json, itemType)
    }

    override fun onPause() {
        super.onPause()
        audioplayerViewModel.pausePlayer()
    }

    override fun onResume() {
        super.onResume()
        audioplayerViewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioplayerViewModel.stopPlayer()
        _binding = null
    }
}