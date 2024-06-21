package com.example.playlistmaker.search.domain.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.library.data.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory,
    private val context: Context,
    private val appDatabase: AppDatabase
) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(context.getString(R.string.st_no_internet)))
            }

            200 -> {
                val data = (response as TracksSearchResponse).results.map{
                    if(getTrackIdList().contains(it.trackId)){
                        it.mapToDomain().copy(isFavorite = true)
                    }
                    else{
                        it.mapToDomain()
                    }
                }
                emit(Resource.Success(data))
            }

            else -> {
                emit(Resource.Error(context.getString(R.string.st_server_error)))
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

    private suspend fun getTrackIdList(): List<Int>{
        val idList = appDatabase.trackDao().getTracksIdList()
        return idList ?: emptyList()
    }
}