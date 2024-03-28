package com.example.playlistmaker.ui.search

import com.example.playlistmaker.App
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory() {

    private val savedHistory = App.getSharedPreferences()
    private val gson = Gson()
    var searchedTrackList = App.mediaHistoryList

    fun editArray(newHistoryTrack: Track) {
        if (searchedTrackList.contains(newHistoryTrack)) {
            searchedTrackList.remove(newHistoryTrack)
        } else if (searchedTrackList.size >= 10) {
            searchedTrackList.removeAt(9)
        }
        searchedTrackList.add(0, newHistoryTrack)
        saveHistory()
    }

    private fun saveHistory() {
        val json: String = gson.toJson(searchedTrackList)
        savedHistory.edit().putString(SEARCHED_TRACKS_PREF_KEY, json).apply()
    }

    fun loadHistory() {
        val json = savedHistory.getString(SEARCHED_TRACKS_PREF_KEY, null)
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        searchedTrackList = gson.fromJson(json, type) ?: ArrayList()
    }
    fun clearHistory() {
        searchedTrackList.clear()
        saveHistory()
    }
}