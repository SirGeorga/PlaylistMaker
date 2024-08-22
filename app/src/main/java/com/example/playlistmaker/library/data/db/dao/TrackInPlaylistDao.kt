package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(entity = TrackInPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(trackInPlaylistEntity: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table")
    suspend fun getTracks(): List<TrackInPlaylistEntity>?

    @Delete(entity = TrackInPlaylistEntity::class)
    suspend fun deleteTrackFromPlaylist(trackInPlaylistEntity: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): TrackInPlaylistEntity
}