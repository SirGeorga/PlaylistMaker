package com.example.playlistmaker.library.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentChosenPlaylistBinding
import com.example.playlistmaker.library.ui.view_model.ChosenPlaylistViewModel
import com.example.playlistmaker.player.ui.fragments.PlayerFragment
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.fragment.SearchFragment
import com.example.playlistmaker.search.ui.recycler_view.ChosenPlaylistTrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ChosenPlaylistFragment : Fragment() {

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    companion object {
        private const val PLAYLISTID = "playlistId"
        fun createArgs(playlistId: Int): Bundle = bundleOf(
            PLAYLISTID to playlistId
        )
    }

    private val trackAdapter = ChosenPlaylistTrackAdapter({ track ->
        onTrackClickDebounce(track)
    }, { track ->
        chosenPlaylistViewModel.currentTrack = track
        showConfirmDialogDeleteTrack()
    })

    private val chosenPlaylistViewModel by viewModel<ChosenPlaylistViewModel>()
    private var _binding: FragmentChosenPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChosenPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            SearchFragment.SEARCH_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track ->
            findNavController().navigate(
                R.id.action_chosenPlaylistFragment_to_playerFragment,
                PlayerFragment.createArgs(createJsonFromTrack(track))
            )
        }

        val convertedPlaylistId = requireArguments().getInt(PLAYLISTID)


        chosenPlaylistViewModel.getPlaylistById(convertedPlaylistId)

        chosenPlaylistViewModel.observeChosenPlaylist().observe(viewLifecycleOwner) {
            if (it !== null) {
                with(binding) {
                    tvPlaylistName.text = it.playlistName
                    tvDescription.text = it.playlistDescription
                    tvNumberOfTracks.text = resources.getQuantityString(
                        R.plurals.plurals_tracks_add, it.numberOfTracks, it.numberOfTracks
                    )
                    with(innerPlaylistItem) {
                        playlistName.text = it.playlistName
                        numberOfTracks.text = it.numberOfTracks.toString()
                        tracks.text = resources.getQuantityString(
                            R.plurals.plurals_tracks, it.numberOfTracks
                        )
                    }
                    showTracklist(it.numberOfTracks)
                    Glide.with(this@ChosenPlaylistFragment).load(it.imageFilePath).centerCrop()
                        .placeholder(R.drawable.ic_playlist_placeholder)
                        .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.dm_corner_radius)))
                        .into(binding.ivPlaylistCover)

                    Glide.with(this@ChosenPlaylistFragment).load(it.imageFilePath).centerCrop()
                        .placeholder(R.drawable.ic_playlist_placeholder)
                        .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.dm_corner_radius_small)))
                        .into(binding.innerPlaylistItem.playlistImage)
                }
            } else findNavController().popBackStack()
        }

        chosenPlaylistViewModel.observeTrackList().observe(viewLifecycleOwner) {
            binding.tvMinutes.text = showSumTracksTime(it, requireContext())
            trackAdapter.setTracks(it)
        }

        binding.rvBottomSheet.adapter = trackAdapter

        val bottomSheetMoreBehavior = BottomSheetBehavior.from(binding.llBottomSheetShare).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        val bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.llBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED

        }

        bottomSheetMoreBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.vOverlay.isVisible = false
                        bottomSheetTracksBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                    else -> {
                        binding.vOverlay.isVisible = true
                        bottomSheetTracksBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.ibBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.ibShare.setOnClickListener {
            sharePlaylist()
        }

        binding.ibMore.setOnClickListener {
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.flBottomSheetShare.setOnClickListener {
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.flBottomSheetEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_chosenPlaylistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(chosenPlaylistViewModel.currentPlaylist.playlistId)
            )
        }

        binding.flBottomSheetDelete.setOnClickListener {
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showConfirmDialogDeletePlaylist()

        }
    }

    private fun showConfirmDialogDeletePlaylist() {
        val stringDelete =
            requireContext().resources.getString(R.string.st_do_you_want_to_delete_playlist)
        val confirmDialogDeletePlaylistBuilder =
            MaterialAlertDialogBuilder(requireContext()).setTitle("$stringDelete \u00AB${chosenPlaylistViewModel.currentPlaylist.playlistName}\u00BB?")
                .setNegativeButton(R.string.st_no) { dialog, which ->
                }.setPositiveButton(R.string.st_yes) { dialog, which ->
                    chosenPlaylistViewModel.deletePlaylist()

                }
        confirmDialogDeletePlaylistBuilder.show()
    }

    private fun showConfirmDialogDeleteTrack() {
        val confirmDialogDeleteTrackBuilder =
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.st_do_you_want_to_delete_track)
                .setNegativeButton(R.string.st_no) { dialog, which ->
                }.setPositiveButton(R.string.st_yes) { dialog, which ->
                    chosenPlaylistViewModel.deleteTrackFromPlaylist()
                }
        confirmDialogDeleteTrackBuilder.show()
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }


    private fun sharePlaylist() {
        if (chosenPlaylistViewModel.currentPlaylist.numberOfTracks == 0) {
            Toast.makeText(
                requireContext(), R.string.st_theres_no_track_to_share, Toast.LENGTH_LONG
            ).show()
        } else {
            chosenPlaylistViewModel.sharePlaylist(requireContext())
        }
    }

    private fun showTracklist(numberOfTracks: Int) {
        if (numberOfTracks == 0) {
            binding.rvBottomSheet.isVisible = false
            binding.tvPlaceholderText.isVisible = true
        } else {
            binding.rvBottomSheet.isVisible = true
            binding.tvPlaceholderText.isVisible = false
        }
    }

    fun showSumTracksTime(trackList: List<Track>, context: Context): String {
        val totalTimeLong = trackList.sumOf { it.trackTimeMillis.toLong() }
        val totalTimeString =
            SimpleDateFormat("mm", Locale.getDefault()).format(totalTimeLong.toInt())
        return context.resources.getQuantityString(
            R.plurals.plurals_minutes, totalTimeString.toInt(), totalTimeString
        )
    }
}