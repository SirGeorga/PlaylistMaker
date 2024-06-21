package com.example.playlistmaker.library.data.db.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity

interface TrackDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY addTime DESC")
    suspend fun getTracks(): List<TrackEntity>?

    @Query("SELECT trackId FROM track_table")
    suspend fun getTracksIdList(): List<Int>?
}