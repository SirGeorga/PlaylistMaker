package com.example.playlistmaker

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Int, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val trackId: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val track = other as Track
        return trackId == track.trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}