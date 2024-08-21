package com.example.playlistmaker.player.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.ui.recycler_view.PlaylistAdapter

class PlaylistBottomSheetAdapter(val clickListener: PlaylistAdapter.PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistBottomSheetViewHolder>() {

    private val playlists = mutableListOf<Playlist>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistBottomSheetViewHolder =
        PlaylistBottomSheetViewHolder(parent)

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists.get(position)) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun setPlaylists(newPlaylists: List<Playlist>?) {
        playlists.clear()
        if (!newPlaylists.isNullOrEmpty()) {
            playlists.addAll(newPlaylists)
        }
        notifyDataSetChanged()
    }
}