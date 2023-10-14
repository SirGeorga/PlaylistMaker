package com.example.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MediaAdapter(var tracks: ArrayList<Track>) :
    RecyclerView.Adapter<MediaViewHolder>() {

    /*var tracks = ArrayList<Track>()
    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
    */

    private val searchActivityObj = SearchActivity()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {

/*        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(tracks[position])
        }*/

        App.getSharedPreferences()
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("Track Name", tracks[position].trackName)
            intent.putExtra("Artist Name", tracks[position].artistName)
            val trackTime = SimpleDateFormat("mm:ss",
                Locale.getDefault()).format(tracks[position].trackTimeMillis)
            intent.putExtra("Track Time", trackTime)
            intent.putExtra("Album", tracks[position].collectionName)
            intent.putExtra("Year", tracks[position].releaseDate)
            intent.putExtra("Genre", tracks[position].primaryGenreName)
            intent.putExtra("Country", tracks[position].country)
            intent.putExtra("Cover", tracks[position].artworkUrl100)
            holder.itemView.context.startActivity(intent)

            searchActivityObj.searchHistoryObj.editArray(tracks[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = tracks.size
}