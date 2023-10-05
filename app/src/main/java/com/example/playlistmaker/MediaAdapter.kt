package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView

class MediaAdapter(val clickListener: TrackClickListener) :
    RecyclerView.Adapter<MediaViewHolder>() {

    var tracks = ArrayList<Track>()

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(tracks[position])
        }
    }

    override fun getItemCount() = tracks.size
}