package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(val sharedPreferences: SharedPreferences) {
    val searchedTrackList = ArrayList<Track>()
    private val searchListLimit = 10

    init {
        val searchedTrack = sharedPreferences.getString(SEARCHED_TRACKS_LIST_KEY, "").orEmpty()
        if (searchedTrack.isNotEmpty()) {
            searchedTrackList.addAll(createTrackListFromJson(searchedTrack))
        }
    }

    fun addNewTrack(track: Track) {
        if (searchedTrackList.contains(track)) searchedTrackList.remove(track)
        searchedTrackList.add(0, track)

        if (searchedTrackList.size > searchListLimit) searchedTrackList.removeAt(searchListLimit)
        sharedPreferences.edit()
            .putString(
                SEARCHED_TRACKS_LIST_KEY,
                createJsonFromTrackList(searchedTrackList.toTypedArray())
            )
            .apply()
    }

    fun clearHistory() {
        searchedTrackList.clear()
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    // метод десерриализует массив объектов Track (в Shared Preference они хранятся в виде json строки)
    fun createTrackListFromJson(json: String?): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    // метод серриализует массив объектов Track (переводит в формат json)
    fun createJsonFromTrackList(srchdTracks: Array<Track>): String {
        return Gson().toJson(srchdTracks)
    }

}