package com.example.playlistmaker.library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.library.ui.view_model.FavouritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavouritesFragment().apply {
        }
    }

    private val favouritesViewModel: FavouritesViewModel by viewModel()
    private lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}