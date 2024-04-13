package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.search.domain.model.Track

class TracksSearchResponse(
    val resultCount: Int,
    val results: ArrayList<Track>
 //   val results: ArrayList<TrackDto>
) : Response()