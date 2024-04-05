package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

class TracksSearchResponse(
    val resultCount: Int,
    val results: ArrayList<Track>
 //   val results: ArrayList<TrackDto>
) : Response()