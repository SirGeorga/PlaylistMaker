package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.networkModule.ITunesApi
import com.example.playlistmaker.networkModule.SongsSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SEARCHED_TRACKS_PREFERENCES = "searched_tracks_preferences"
const val SEARCHED_TRACKS_LIST_KEY = "key_for_searched_tracks_list"

class SearchActivity : AppCompatActivity(), MediaAdapter.TrackClickListener {

    private var searchPhrase: String = ""
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_PHRASE, searchPhrase)
    }

    private lateinit var placeHolderLayout: LinearLayout
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var placeHolderImage: ImageView
    private lateinit var backButton: TextView
    private lateinit var clearButton: ImageView
    private lateinit var placeHolderText: TextView
    private lateinit var updateButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearHistoryButton: Button
    private lateinit var historyHeader: TextView
    private lateinit var historyScrollView: NestedScrollView
    private lateinit var searchHistoryObj: SearchHistory
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPrefChangelistener: SharedPreferences.OnSharedPreferenceChangeListener

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit =
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private val historyTracks = ArrayList<Track>()

    val mediaAdapter = MediaAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initListeners()

        updateMediaAdapter(historyTracks)
        tracksRecyclerView.adapter = mediaAdapter
        sharedPreferences = getSharedPreferences(SEARCHED_TRACKS_PREFERENCES, MODE_PRIVATE)
        searchHistoryObj = SearchHistory(sharedPreferences)
        historyTracks.addAll(searchHistoryObj.searchedTrackList)
        historyInVisible()

        sharedPrefChangelistener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == SEARCHED_TRACKS_LIST_KEY) {
                    val searchedMedia = sharedPreferences?.getString(SEARCHED_TRACKS_LIST_KEY, null)
                    if (searchedMedia != null) {
                        historyTracks.clear()
                        historyTracks.addAll(searchHistoryObj.createTrackListFromJson(searchedMedia))
                        updateMediaAdapter(historyTracks)
                    }
                }
            }

        clearHistoryButton.setOnClickListener {
            searchHistoryObj.clearHistory()
            historyTracks.clear()
            updateMediaAdapter(historyTracks)
            historyInVisible()
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPrefChangelistener)

        searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty() && historyTracks.isNotEmpty()) {
                updateMediaAdapter(historyTracks)
                historyVisible()
            } else {
                historyInVisible()
            }
        }

        if (savedInstanceState != null) {
            searchEditText.setText(savedInstanceState.getString(SEARCH_PHRASE, ""))
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchPhrase = searchEditText.text.toString()
                showMessage("", 0, false, "")

                if (searchEditText.hasFocus() && s?.isNullOrEmpty() == true && historyTracks.isNotEmpty()) {
                    updateMediaAdapter(historyTracks)
                    historyVisible()
                } else {
                    historyInVisible()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun updateMediaAdapter(source: ArrayList<Track>) {
        mediaAdapter.tracks = source
        mediaAdapter.notifyDataSetChanged()
    }

    private fun initListeners() {
        backButton.setOnClickListener {
            finish()
        }
        clearButton.setOnClickListener {
            searchEditText.setText("")
            tracks.clear()
            updateMediaAdapter(tracks)
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
        }
        updateButton.setOnClickListener {
            searchQuery()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                historyInVisible()
                searchQuery()
                true
            }
            false
        }
    }

    private fun initViews() {
        placeHolderLayout = findViewById(R.id.llSearchPlaceholder)
        tracksRecyclerView = findViewById(R.id.tracksRecyclerView)
        placeHolderImage = findViewById(R.id.ivSearchPlaceholderImg)
        placeHolderText = findViewById(R.id.tvSearchPlaceholderTxt)
        updateButton = findViewById(R.id.btSearchUpdate)
        searchEditText = findViewById(R.id.searchEditText)
        backButton = findViewById(R.id.bt_search_back)
        clearButton = findViewById(R.id.clearIcon)
        historyHeader = findViewById(R.id.tvHistoryHeader)
        clearHistoryButton = findViewById(R.id.bt_clear_search_history)
        historyScrollView = findViewById(R.id.nsvHistory)
    }

    private fun searchQuery() {
        if (searchEditText.text.isNotEmpty()) {
            iTunesService.search(searchEditText.text.toString())
                .enqueue(object : Callback<SongsSearchResponse> {
                    override fun onResponse(
                        call: Call<SongsSearchResponse>, response: Response<SongsSearchResponse>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                historyScrollView.visibility = View.VISIBLE
                                tracks.addAll(response.body()?.results!!)
                                updateMediaAdapter(tracks)
                                tracksRecyclerView.visibility = View.VISIBLE
                            }
                            if (tracks.isEmpty()) {
                                historyScrollView.visibility = GONE
                                showMessage(
                                    getString(R.string.st_nothing_found),
                                    R.drawable.ic_search_no_item,
                                    false,
                                    ""
                                )
                            } else {
                                showMessage("", 0, false, "")
                            }
                        } else {
                            historyScrollView.visibility = GONE
                            showMessage(
                                getString(R.string.st_no_internet),
                                R.drawable.ic_search_no_internet,
                                true,
                                response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<SongsSearchResponse>, t: Throwable) {
                        tracks.clear()
                        showMessage(
                            getString(R.string.st_no_internet),
                            R.drawable.ic_search_no_internet,
                            true,
                            t.message.toString()
                        )
                    }

                })
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //showMessage - функция динамического отображения плэйсхолдера при ошибках
    //text - текст плэйсхолдера
    //imgRes - номер изображения из ресурсов
    //updBtn - видимость кнопки "Обновить" - true/false
    //additionalMessage - дополнительное сообщение для отладки
    ////////////////////////////////////////////////////////////////////////////
    private fun showMessage(text: String, imgRes: Int, updBtn: Boolean, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeHolderLayout.visibility = View.VISIBLE
            tracks.clear()
            updateMediaAdapter(tracks)
            placeHolderText.text = text
            placeHolderImage.setImageResource(imgRes)
            if (updBtn) updateButton.visibility = View.VISIBLE
            else updateButton.visibility = GONE
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeHolderLayout.visibility = GONE
        }
    }

    private fun historyVisible() {
        if (historyTracks.isNotEmpty()) {
            historyScrollView.visibility = View.VISIBLE
            historyHeader.visibility = View.VISIBLE
            tracksRecyclerView.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }

    private fun historyInVisible() {
        historyScrollView.visibility = GONE
        historyHeader.visibility = GONE
        tracksRecyclerView.visibility = GONE
        clearHistoryButton.visibility = GONE
    }

    companion object {
        const val SEARCH_PHRASE = "SEARCH_PHRASE"
    }

    override fun onTrackClick(track: Track) {
        searchHistoryObj.addNewTrack(track)
        historyVisible()
    }
}