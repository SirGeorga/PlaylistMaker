package com.example.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class MediaAdapter(var tracks: ArrayList<Track>, private val searchHistoryObj: SearchHistory) :
    RecyclerView.Adapter<MediaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        App.getSharedPreferences()
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java).apply {
                putExtra("track", tracks[position])
            }
            searchHistoryObj.editArray(tracks[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = tracks.size
}