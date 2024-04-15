package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.TracksState
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val historyLimit = 10

class TrackSearchViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    private var savedHistoryList: ArrayList<Track> = ArrayList()

    init {
        savedHistoryList.addAll(createTrackListFromJson(tracksInteractor.loadHistory().toString()))
    }

    fun observeShowToast(): LiveData<String> = showToast

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                TracksState.Error(
                                    errorMessage = getApplication<Application>().getString(R.string.st_no_internet),
                                )
                            )
                            showToast.postValue(errorMessage!!)
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                TracksState.Empty(
                                    message = getApplication<Application>().getString(R.string.st_nothing_found),
                                )
                            )
                        }

                        else -> {
                            renderState(
                                TracksState.Content(
                                    tracks = tracks,
                                )
                            )
                        }
                    }
                }
            })
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