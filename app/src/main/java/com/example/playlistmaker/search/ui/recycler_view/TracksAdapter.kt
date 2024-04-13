package com.example.playlistmaker.search.ui.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.model.Track
import kotlin.collections.ArrayList


class TracksAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent, clickListener)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}

/*
class TracksAdapter(var tracks: ArrayList<Track>, private val searchHistoryObj: SearchHistory) :
    RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        App.getSharedPreferences()
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val intent = Intent(holder.itemView.context, PlayerActivity::class.java).apply {
                    putExtra("track", tracks[position])
                }
                searchHistoryObj.editArray(tracks[position])
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = tracks.size

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}

 */