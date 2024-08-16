package com.example.playlistmaker.search.ui.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Playlist

class PlaylistViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item, parent, false)
    ) {

    private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImage)
    private val titlePlayist: TextView = itemView.findViewById(R.id.titlePlayist)
    private val numberOfTracks: TextView = itemView.findViewById(R.id.numberOfTracks)
    private val tracks: TextView = itemView.findViewById(R.id.tracks)

    fun bind(item: Playlist) {

        Glide.with(itemView)
            .load(item.imageFilePath)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.search_bar_corner_radius_8dp)))
            .into(playlistImage)

        titlePlayist.text = item.playlistName
        numberOfTracks.text = item.numberOfTracks.toString()
        tracks.text =
            itemView.resources.getQuantityString(R.plurals.plurals_track, item.numberOfTracks)
    }
}