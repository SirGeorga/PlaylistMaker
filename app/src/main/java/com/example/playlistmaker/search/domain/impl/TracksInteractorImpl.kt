package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.data.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute{
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {consumer.consume(resource.data, null)}
                is Resource.Error -> {consumer.consume(null, resource.message)}
            }
        }
    }
}