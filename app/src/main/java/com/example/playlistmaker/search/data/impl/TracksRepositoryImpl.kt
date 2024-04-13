package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.data.api.TracksRepository
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.model.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory
) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    it.mapToDomain()
                })
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun loadHistoryFromPref(): String? {
        return searchHistory.loadTracksFromPref()
    }

    override fun saveHistoryToPref(json: String) {
        searchHistory.safeHistoryToPref(json)
    }

    override fun clearHistoryPref() {
        searchHistory.clearHistoryPref()
    }

    private fun Track.mapToDomain(): Track {
        return Track(
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100,
            trackId = trackId,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country,
            previewUrl = previewUrl,
            false
        )
    }
}