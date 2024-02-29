package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

const val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"
const val SEARCHED_TRACKS_PREF_KEY = "searched_tracks_list"

class SearchActivity : AppCompatActivity() {


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_PHRASE, searchPhrase)
    }

    private val searchRunnable = Runnable { searchQuery() }
    private val handler = Handler(Looper.getMainLooper())
    private var searchPhrase: String = ""
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
    private lateinit var tracksScrollView: NestedScrollView
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var progressBar: ProgressBar

    var searchHistoryObj = SearchHistory()

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit =
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private var tracks = ArrayList<Track>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initListeners()

        if (savedInstanceState != null) {
            searchEditText.setText(savedInstanceState.getString(SEARCH_PHRASE, ""))
        }
        searchHistoryObj.loadHistory()
        mediaAdapter.tracks = tracks
        tracksRecyclerView.adapter = mediaAdapter
        historyInVisible()
    }

    private fun initListeners() {

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchPhrase = searchEditText.text.toString()
                if (searchEditText.hasFocus() && s?.isEmpty() == true && searchHistoryObj.searchedTrackList.isNotEmpty()) {
                    updateMediaAdapter(searchHistoryObj.searchedTrackList)
                    historyVisible()
                } else {
                    historyInVisible()
                }
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)

        searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty() && searchHistoryObj.searchedTrackList.isNotEmpty()) {
                updateMediaAdapter(searchHistoryObj.searchedTrackList)
                historyVisible()
            } else {
                historyInVisible()
            }
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryObj.clearHistory()
            tracks.clear()
            tracks.addAll(searchHistoryObj.searchedTrackList)
            updateMediaAdapter(tracks)
            historyInVisible()
        }

        backButton.setOnClickListener {
            finish()
        }
        updateButton.setOnClickListener {

            searchQuery()
        }
        clearButton.setOnClickListener {
            searchEditText.setText("")
            tracks.clear()
            updateMediaAdapter(tracks)
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
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
        tracksScrollView = findViewById(R.id.nsvTracksInterfaceContainer)
        mediaAdapter = MediaAdapter(tracks, searchHistoryObj)
        progressBar = findViewById(R.id.progressBar)
    }


    private fun updateMediaAdapter(source: ArrayList<Track>) {
        val copyList = ArrayList(source)
        mediaAdapter.tracks = copyList
        mediaAdapter.notifyDataSetChanged()
    }

    private fun searchQuery() {
        if (searchEditText.text.isNotEmpty()) {
            showMessage("",0,false,"")
            progressBar.visibility = View.VISIBLE
            iTunesService.search(searchEditText.text.toString())
                .enqueue(object : Callback<SongsSearchResponse> {
                    override fun onResponse(
                        call: Call<SongsSearchResponse>, response: Response<SongsSearchResponse>
                    ) {
                        progressBar.visibility = GONE
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracksScrollView.visibility = View.VISIBLE
                                tracks.addAll(response.body()?.results!!)
                                updateMediaAdapter(tracks)
                                tracksRecyclerView.visibility = View.VISIBLE
                            }
                            if (tracks.isEmpty()) {
                                tracksScrollView.visibility = GONE
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
                            tracksScrollView.visibility = GONE
                            showMessage(
                                getString(R.string.st_no_internet),
                                R.drawable.ic_search_no_internet,
                                true,
                                response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<SongsSearchResponse>, t: Throwable) {
                        progressBar.visibility = GONE
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
            updateButton.visibility = when (updBtn) {
                true -> View.VISIBLE
                else -> GONE
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeHolderLayout.visibility = GONE
        }
    }

    private fun historyVisible() {
        tracksScrollView.visibility = View.VISIBLE
        historyHeader.visibility = View.VISIBLE
        tracksRecyclerView.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
    }

    private fun historyInVisible() {
        tracksScrollView.visibility = GONE
        historyHeader.visibility = GONE
        tracksRecyclerView.visibility = GONE
        clearHistoryButton.visibility = GONE
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val SEARCH_PHRASE = "SEARCH_PHRASE"
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
    }
}