package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConverter {
        fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId.toLong(),
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavourite,
            System.currentTimeMillis())
    }

    fun map(trackEntity: TrackEntity): Track{
        return Track(
            trackEntity.trackId.toInt(),
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl,
            trackEntity.isFavourite)
    }
}