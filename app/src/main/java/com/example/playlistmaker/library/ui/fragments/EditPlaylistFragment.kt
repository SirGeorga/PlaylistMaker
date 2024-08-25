package com.example.playlistmaker.library.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.view_model.EditPlaylistViewModel
import com.example.playlistmaker.search.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    companion object {
        private const val PLAYLISTID = "playlistId"
        fun createArgs(playlistId: Int): Bundle = bundleOf(
            PLAYLISTID to playlistId
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val convertedPlaylistId = requireArguments().getInt(PLAYLISTID)

        binding.llButtonBack.text = resources.getString(R.string.st_edit)
        binding.bCreatePlaylistButton.text = resources.getString(R.string.st_save)
        viewModel.getPlaylistById(convertedPlaylistId)

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            if (it !== null) {
                currentPlaylist = it
                binding.etNewPlaylistNameInput.setText(it.playlistName)
                binding.etDescriptionInput.setText(it.playlistDescription)
                binding.ivNewPlaylistImage.background = null

                Glide.with(this).load(it.imageFilePath).centerCrop()
                    .placeholder(R.drawable.ic_playlist_placeholder)
                    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.dm_corner_radius)))
                    .into(binding.ivNewPlaylistImage)
            } else findNavController().popBackStack()
        }

        binding.llButtonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.bCreatePlaylistButton.setOnClickListener {
            createPlaylist(currentPlaylist)
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

    }

    override fun createPlaylist(playlist: Playlist) {
        if (currentUri == null) {
            imageFilePath = playlist.imageFilePath
        } else saveImageToPrivateStorage(currentUri)
        viewModel.addPlaylist(
            playlist.copy(
                playlistName = binding.etNewPlaylistNameInput.text.toString(),
                playlistDescription = binding.etDescriptionInput.text.toString(),
                imageFilePath = imageFilePath ?: setPlaceholder(),
            )
        )
    }
}