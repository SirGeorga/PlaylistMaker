package com.example.playlistmaker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.library.ui.model.FavouriteState
import com.example.playlistmaker.library.ui.view_model.FavouritesViewModel
import com.example.playlistmaker.player.ui.fragments.PlayerFragment
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.fragment.SearchFragment
import com.example.playlistmaker.search.ui.recycler_view.TracksAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : Fragment() {

    private val trackAdapter = TracksAdapter {
        onTrackClickDebounce(it)
    }

    private val favouritesViewModel by viewModel<FavouritesViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivFavouritesPlaceholderImg.setImageResource(R.drawable.ic_search_no_item)
        binding.tvPlaylistsPlaceholderTxt.setText(R.string.st_favourites_empty)
        binding.rvFavourites.adapter = trackAdapter

        onTrackClickDebounce = debounce<Track>(
            SearchFragment.SEARCH_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { track ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                PlayerFragment.createArgs(createJsonFromTrack(track))
            )
        }

        favouritesViewModel.observeFavorites().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavouriteState) {
        when (state) {
            is FavouriteState.Empty -> showEmpty()
            is FavouriteState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        binding.rvFavourites.isVisible = false
        binding.ivFavouritesPlaceholderImg.isVisible = true
        binding.tvPlaylistsPlaceholderTxt.isVisible = true
    }

    private fun showContent(trackList: List<Track>) {
        binding.rvFavourites.isVisible = true
        binding.ivFavouritesPlaceholderImg.isVisible = false
        binding.tvPlaylistsPlaceholderTxt.isVisible = false
        trackAdapter.updateMediaAdapter(trackList as ArrayList<Track>)
    }

    override fun onResume() {
        super.onResume()
        favouritesViewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    companion object {
        fun newInstance() = FavouritesFragment()
    }
}