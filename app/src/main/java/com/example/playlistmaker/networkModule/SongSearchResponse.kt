    package com.example.playlistmaker.networkModule

import com.example.playlistmaker.Track

class SongsSearchResponse(val resultCount: Int,
                          val results: ArrayList<Track>)