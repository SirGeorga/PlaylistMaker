package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.data.db.entity.TrackInPlaylistEntity
import com.example.playlistmaker.search.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {
    private val itemType = object : TypeToken<ArrayList<Int>>() {}.type

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.imageFilePath,
            createJsonFromTrackIdList(playlist.trackIdList),
            playlist.numberOfTracks,
            System.currentTimeMillis()
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistName,
            playlistEntity.playlistDescription,
            playlistEntity.imageFilePath,
            createTrackIdListFromJson(playlistEntity.trackIdList),
            playlistEntity.numberOfTracks
        )
    }

    fun map(track: Track): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis.toLong(),
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            System.currentTimeMillis()
        )
    }

    fun map(trackInPlaylistEntity: TrackInPlaylistEntity): Track {
        return Track(
            trackId = trackInPlaylistEntity.trackId,
            trackName = trackInPlaylistEntity.trackName,
            artistName = trackInPlaylistEntity.artistName,
            trackTimeMillis = trackInPlaylistEntity.trackTimeMillis.toString(),
            artworkUrl100 = trackInPlaylistEntity.artworkUrl100,
            collectionName = trackInPlaylistEntity.collectionName,
            releaseDate = trackInPlaylistEntity.releaseDate,
            primaryGenreName = trackInPlaylistEntity.primaryGenreName,
            country = trackInPlaylistEntity.country,
            previewUrl = trackInPlaylistEntity.previewUrl,
        )
    }

    fun map(stringList: List<String>): List<ArrayList<Int>> {
        val list = stringList.map {
            createTrackIdListFromJson(it)
        }
        return list
    }

    private fun createJsonFromTrackIdList(trackIdList: List<Int>): String {
        return Gson().toJson(trackIdList)
    }

    private fun createTrackIdListFromJson(json: String?): ArrayList<Int> {
        return Gson().fromJson(json, itemType)
    }
}