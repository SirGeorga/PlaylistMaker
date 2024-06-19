package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { resource ->
            when (resource) {
                is Resource.Success -> Pair(resource.data, null)
                is Resource.Error -> Pair(null, resource.message)
            }
        }
    }

    override fun loadHistory(): String? {
        return repository.loadHistoryFromPref()
    }

    override fun saveToHistory(json: String) {
        repository.saveHistoryToPref(json)
    }

    override fun clearHistory() {
        repository.clearHistoryPref()
    }
}