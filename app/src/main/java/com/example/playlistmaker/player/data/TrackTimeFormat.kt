package com.example.playlistmaker.player.data

import java.text.SimpleDateFormat
import java.util.Locale

class TrackTimeFormat {
    fun convertTrackTimeToString(timeMillis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }
}