package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.debounce
import com.example.playlistmaker.search.data.TracksState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

const val historyLimit = 10

class TrackSearchViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    private var savedHistoryList: ArrayList<Track> = ArrayList()

    var isScreenPaused: Boolean = false

    init {
        savedHistoryList.addAll(createTrackListFromJson(tracksInteractor.loadHistory().toString()))
    }

    fun observeShowToast(): SingleLiveEvent<String?> = showToast

    private var latestSearchText: String? = null
    private var trackState: TracksState? = null

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText || changedText == "") {
            return
        }
        trackSearchDebounce(changedText)
    }

    fun searchRequest(newSearchText: String) {
        if (trackState != null) stateLiveData.postValue(trackState!!)
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)
            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        if (!isScreenPaused) processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        if (foundTracks != null) {
            when {
                errorMessage != null -> {
                    renderState(
                        TracksState.Error(
                            errorMessage = R.string.st_no_internet
                        )
                    )
                    showToast.postValue(errorMessage)
                }

                foundTracks.isEmpty() -> {
                    renderState(
                        TracksState.Empty(
                            message = R.string.st_nothing_found
                        )
                    )
                }

                else -> {
                    renderState(
                        TracksState.Content(
                            tracks = foundTracks
                        )
                    )
                }
            }
        }
    }

    private fun editHistoryList(trackList: List<Track>?) {
        if (trackList.isNullOrEmpty()) tracksInteractor.clearHistory()
        else tracksInteractor.saveToHistory(
            createJsonFromTrackList(
                trackList
            )
        )
    }

    fun addTrackToHistory(track: Track) {
        if (savedHistoryList.contains(track)) {
            savedHistoryList.remove(track)
        } else if (savedHistoryList.size >= historyLimit) {
            savedHistoryList.removeAt(historyLimit - 1)
        }
        savedHistoryList.add(0, track)
        editHistoryList(savedHistoryList)
    }

    private fun createJsonFromTrackList(trackList: List<Track>): String {
        return Gson().toJson(trackList)
    }

    private fun createTrackListFromJson(json: String?): ArrayList<Track> {
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type) ?: ArrayList()
    }

    private fun renderState(state: TracksState) {
        trackState = state
        stateLiveData.postValue(state)
    }

    fun getCurrentHistoryList(): ArrayList<Track> {
        return savedHistoryList
    }

    fun clearHistory() {
        savedHistoryList.clear()
        tracksInteractor.clearHistory()
    }
}