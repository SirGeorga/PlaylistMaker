package com.example.playlistmaker.search.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.model.Track


class TracksAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent, clickListener)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun updateMediaAdapter(source: ArrayList<Track>) {
        val copyList = ArrayList(source)
        tracks = copyList
        notifyDataSetChanged()
    }
}