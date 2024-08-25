package com.example.playlistmaker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.model.PlaylistsGridState
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.ui.fragment.SearchFragment
import com.example.playlistmaker.search.ui.recycler_view.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private val playlistAdapter = PlaylistAdapter {
        onPlaylistClickDebounce(it)
    }
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistClickDebounce = debounce<Playlist>(
            SearchFragment.SEARCH_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false
        ) { playlist ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_chosenPlaylistFragment,
                ChosenPlaylistFragment.createArgs(playlist.playlistId)
            )
        }


        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = playlistAdapter
        binding.ivPlaylistsPlaceholderImg.setImageResource(R.drawable.ic_search_no_item)
        binding.tvPlaylistsPlaceholderTxt.setText(R.string.st_playlists_empty)


        binding.btNewPlaylist.isVisible = true

        playlistsViewModel.observePlaylists().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.btNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }
    }

    private fun render(state: PlaylistsGridState) {
        when (state) {
            is PlaylistsGridState.Empty -> showEmpty()
            is PlaylistsGridState.Content -> showContent(state.playlists)
        }
    }

    private fun showEmpty() {
        binding.ivPlaylistsPlaceholderImg.isVisible = true
        binding.tvPlaylistsPlaceholderTxt.isVisible = true
        binding.playlistRecyclerView.isVisible = false
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.ivPlaylistsPlaceholderImg.isVisible = false
        binding.tvPlaylistsPlaceholderTxt.isVisible = false
        binding.playlistRecyclerView.isVisible = true
        playlistAdapter.setPlaylistsGrid(playlists)
    }

    override fun onResume() {
        super.onResume()
        playlistsViewModel.fillData()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}