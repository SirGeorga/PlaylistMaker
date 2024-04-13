package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.data.NetworkClient

class TracksRepositoryImpl (private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    Track(it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.trackId,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                        false)})
            } else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}