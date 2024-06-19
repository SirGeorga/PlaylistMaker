package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun loadHistoryFromPref(): String?
    fun saveHistoryToPref(json: String)
    fun clearHistoryPref()

}