package com.example.playlistmaker.library.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.library.ui.model.FavouriteState
import com.example.playlistmaker.library.ui.view_model.FavouritesViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.fragment.SearchFragment
import com.example.playlistmaker.search.ui.recycler_view.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavouritesFragment : Fragment() {

    companion object {
        private const val FAVOURITES = "favourites"
        fun newInstance(favouritesTrackAdapter: String) = FavouritesFragment().apply {
            arguments = Bundle().apply {
                putString(FAVOURITES, favouritesTrackAdapter)
            }
        }
    }

    private val trackAdapter = TracksAdapter {
        onTrackClickDebounce(it)
    }

    private val favouritesViewModel: FavouritesViewModel by viewModel {
        parametersOf(requireArguments().getString(FAVOURITES))
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivFavouritesPlaceholderImg.setImageResource(R.drawable.nothing_found)
        binding.tvPlaylistsPlaceholderTxt.setText(R.string.your_media_is_empty)
        binding.favouriteRecyclerView.adapter = trackAdapter

        //использование корутины с ФАЙЛОМ ДЕБАУНС
        onTrackClickDebounce = debounce<Track>(SearchFragment.SEARCH_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            navigateTo(PlayerActivity::class.java, track)
        }

        favouritesViewModel.observeFavorites().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavouriteState){
        when(state){
            is FavouriteState.Empty -> showEmpty()
            is FavouriteState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty(){
        binding.ivFavouritesPlaceholderImg.visibility = View.VISIBLE
        binding.tvPlaylistsPlaceholderTxt.visibility = View.VISIBLE
        binding.favouriteRecyclerView.visibility = View.GONE
    }

    private fun showContent(trackList: List<Track>){
        binding.favoriteRecyclerView.visibility = View.VISIBLE
        binding.ivFavouritesPlaceholderImg.visibility = View.GONE
        binding.tvPlaylistsPlaceholderTxt.visibility = View.GONE
        trackAdapter.updateMediaAdapter(trackList)
    }

    private fun navigateTo(clazz: Class<out AppCompatActivity>, track: Track) {
        val intent = Intent(requireContext(), clazz)
        intent.putExtra("TRACK", track)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        favouritesViewModel.fillData()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}