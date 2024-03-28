package com.example.playlistmaker.data.dto

import android.os.Parcelable
import com.example.playlistmaker.domain.models.Track
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale


@Parcelize
data class TrackDto(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Int, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val trackId: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String): Parcelable
{
    val year: String
        get() = releaseDate.take(4)
    val trackTimeNormal
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    val artworkUrl512
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
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