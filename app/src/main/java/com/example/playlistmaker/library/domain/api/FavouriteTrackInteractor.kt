package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTrackInteractor {
    suspend fun addTrackToFavorite(track: Track)
    suspend fun deleteTrackFromFavorites(track: Track)
    fun getFavoriteTrackList(): Flow<List<Track>>
}