package com.example.playlistmaker.search.data

import android.content.SharedPreferences


class SearchHistory(val sharedPrefs: SharedPreferences?) {


    private val SEARCHED_TRACKS_PREF_KEY = "searched_tracks_list"
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
}