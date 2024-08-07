package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SearchHistory(val sharedPrefs: SharedPreferences?) {


    private val SEARCHED_TRACKS_PREF_KEY = "searched_tracks_list"

    private val itemType = object : TypeToken<ArrayList<Track>>() {}.type
    fun reloadTracks(): List<Track>? {
        return createTrackListFromJson(sharedPrefs?.getString(SEARCHED_TRACKS_PREF_KEY, "[]"))
    }
    fun loadTracksFromPref(): String? {
        return sharedPrefs?.getString(SEARCHED_TRACKS_PREF_KEY, null)
    }

    fun safeHistoryToPref(trackList: String) {
        sharedPrefs?.edit()
            ?.putString(SEARCHED_TRACKS_PREF_KEY, trackList)
            ?.apply()
    }

    fun clearHistoryPref() {
        sharedPrefs?.edit()
            ?.clear()
            ?.apply()
    }

    private fun createTrackListFromJson(json: String?): ArrayList<Track>{
        return Gson().fromJson(json, itemType)
    }
}