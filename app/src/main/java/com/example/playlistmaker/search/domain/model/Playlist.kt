package com.example.playlistmaker.search.domain.model

data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String = "",
    val playlistDescription: String = "",
    val imageFilePath: String = "",
    val trackIdList: MutableList<Int> = mutableListOf(),
    val numberOfTracks: Int = 0
)
