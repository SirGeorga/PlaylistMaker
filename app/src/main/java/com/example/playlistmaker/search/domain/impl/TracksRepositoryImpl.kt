package com.example.playlistmaker.search.domain.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.model.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory,
    private val context: Context
) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error(context.getString(R.string.st_no_internet))
            }

            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    it.mapToDomain()
                })
            }

            else -> {
                Resource.Error(context.getString(R.string.st_server_error))
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