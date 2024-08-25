package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Update(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY addTime DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>?

    @Delete(entity = PlaylistEntity::class)
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity

    @Query("SELECT trackIdList FROM playlist_table ")
    suspend fun getTracksIdLists(): List<String>?
}