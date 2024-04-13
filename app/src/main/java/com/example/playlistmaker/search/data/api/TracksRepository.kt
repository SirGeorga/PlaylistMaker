package com.example.playlistmaker.search.data.api

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun loadHistoryFromPref(): String?
    fun saveHistoryToPref(json: String)
    fun clearHistoryPref()

}