package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavouriteTrackInteractor
import com.example.playlistmaker.library.domain.api.FavouriteTrackRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavouriteTrackInteractorImpl(private val favoriteTracksRepository: FavouriteTrackRepository) :
    FavouriteTrackInteractor {

    override suspend fun addTrackToFavorite(track: Track) {
        favoriteTracksRepository.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        favoriteTracksRepository.deleteTrackFromFavorites(track)
    }

    override fun getFavoriteTrackList(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTrackList()
    }
}