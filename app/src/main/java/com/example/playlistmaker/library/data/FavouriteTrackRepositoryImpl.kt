package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.api.FavouriteTrackRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.internal.NopCollector.emit

class FavouriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavouriteTrackRepository {

    override suspend fun addTrackToFavorite(track: Track){
        appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
    }

    override fun getFavoriteTrackList(): Flow<List<Track>> = flow{
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks).map{
            it.copy(isFavorite = true)
        })
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>?): List<Track> {
        return tracks?.map { track -> trackDbConverter.map(track) } ?: emptyList()
    }
}