package com.example.playlistmaker.search.ui.recycler_view

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import java.util.Locale

class TrackViewHolder(
    parent: ViewGroup,
    // 1
    private val clickListener: TracksAdapter.TrackClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
) {

    private val trackImage: ImageView = itemView.findViewById(R.id.ivTrackImage)
    private val trackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val trackAuthor: TextView = itemView.findViewById(R.id.tvTrackAuthor)
    private val trackLength: TextView = itemView.findViewById(R.id.tvTrackLength)
    private val roundingRadius = 10
    private var trackId: String = ""

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackAuthor.text = track.artistName
        trackLength.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackId = track.trackId

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(trackImage)

        itemView.setOnClickListener { clickListener.onTrackClick(track) }
    }
}
/*
class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage: ImageView = itemView.findViewById(R.id.ivTrackImage)
    private val trackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val trackAuthor: TextView = itemView.findViewById(R.id.tvTrackAuthor)
    private val trackLength: TextView = itemView.findViewById(R.id.tvTrackLength)
    private val roundingRadius = 10
    private var trackId: String = ""

    fun bind(item: Track) {
        trackName.text = item.trackName
        trackAuthor.text = item.artistName
        trackLength.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)
        trackId = item.trackId

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(trackImage)
    }
}
*/