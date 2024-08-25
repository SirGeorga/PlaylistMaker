package com.example.playlistmaker.search.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.model.Track

class ChosenPlaylistTrackAdapter(
    val clickListener: TrackClickListener, val longClickListener: LongTrackClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {

    private val tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent) { }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks.get(position)) }
        holder.itemView.setOnLongClickListener {
            longClickListener.onLongTrackClick(tracks.get(position))
            true
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface LongTrackClickListener {
        fun onLongTrackClick(track: Track)
    }

    fun setTracks(newTracks: List<Track>?) {
        tracks.clear()
        if (!newTracks.isNullOrEmpty()) {
            tracks.addAll(newTracks)
        }
        notifyDataSetChanged()
    }
}