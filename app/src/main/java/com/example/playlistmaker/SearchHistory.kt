package com.example.playlistmaker

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory() {

    private val savedHistory = App.getSharedPreferences()
    private val gson = Gson()

    private var counter = 0
    var searchedTrackList = App.mediaHistoryList

    fun editArray(newHistoryTrack: Track) {
        val json = ""
        if (json.isNotEmpty()) {
            if (searchedTrackList.isEmpty()) {
                if (savedHistory.contains(SEARCHED_TRACKS_PREF_KEY)) {
                    val type = object : TypeToken<ArrayList<Track>>() {}.type
                    searchedTrackList = gson.fromJson(json, type)
                }
            }
        }
        if (searchedTrackList.contains(newHistoryTrack)) {
            searchedTrackList.remove(newHistoryTrack)
            searchedTrackList.add(0, newHistoryTrack)
        } else {
            if (searchedTrackList.size < 10) searchedTrackList.add(0, newHistoryTrack)
            else {
                searchedTrackList.removeAt(9)
                searchedTrackList.add(0, newHistoryTrack)
            }
        }
        saveHistory()
    }

    private fun saveHistory() {
        var json = ""
        json = gson.toJson(searchedTrackList)
        savedHistory.edit().clear().putString(SEARCHED_TRACKS_PREF_KEY, json).apply()
        counter = searchedTrackList.size
    }

    fun clearHistory() {
        searchedTrackList.clear()
        saveHistory()
    }

}