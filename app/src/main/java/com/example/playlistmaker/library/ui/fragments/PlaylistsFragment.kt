package com.example.playlistmaker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    }
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val playlistsViewModel by viewModel<PlaylistsViewModel>()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var newPlaylist: Button
    private lateinit var placeholderIconPlaylists: ImageView
    private lateinit var placeholderMessagePlaylists: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistClickDebounce = debounce<Playlist>(
            SearchFragment.SEARCH_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->

        }

        playlistRecyclerView = binding.playlistRecyclerView
        newPlaylist = binding.btNewPlaylist
        placeholderIconPlaylists = binding.ivPlaylistsPlaceholderImg
        placeholderMessagePlaylists = binding.tvPlaylistsPlaceholderTxt

        playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        playlistRecyclerView.adapter = playlistAdapter
        placeholderIconPlaylists.setImageResource(R.drawable.ic_search_no_item)
        placeholderMessagePlaylists.setText(R.string.st_playlists_empty)


        newPlaylist.visibility = View.VISIBLE

        playlistsViewModel.observePlaylists().observe(viewLifecycleOwner) {
            render(it)
        }

        newPlaylist.setOnClickListener {
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
        placeholderIconPlaylists.visibility = View.VISIBLE
        placeholderMessagePlaylists.visibility = View.VISIBLE
        playlistRecyclerView.visibility = View.GONE
    }

    private fun showContent(playlists: List<Playlist>) {
        placeholderIconPlaylists.visibility = View.GONE
        placeholderMessagePlaylists.visibility = View.GONE
        playlistRecyclerView.visibility = View.VISIBLE
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