package com.example.playlistmaker.player.ui

data class PlayerState(
    val progress: String,
    val isPlaying: Boolean,
    val prepared: Boolean,
    val completed: Boolean
)
