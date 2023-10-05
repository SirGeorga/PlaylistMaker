package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import java.util.*

class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        trackId = item.mediaId

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(roundingRadius))
            .into(trackImage)
    }
}