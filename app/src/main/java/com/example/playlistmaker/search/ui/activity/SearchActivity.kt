package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.data.TracksState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.recycler_view.TracksAdapter
import com.example.playlistmaker.search.ui.view_model.TrackSearchViewModel

class SearchActivity : AppCompatActivity() {

    private val adapter = TracksAdapter(object : TracksAdapter.TrackClickListener {
        override fun onTrackClick(track: Track) {
            if (searchDebounce()) {
                viewModel.addTrackToHistory(track)
                val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
        }
    })


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_PHRASE, searchPhrase)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var searchPhrase: String = ""
    private lateinit var placeHolderLayout: LinearLayout
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var placeHolderImage: ImageView
    private lateinit var backButton: TextView
    private lateinit var clearButton: ImageView
    private lateinit var placeHolderText: TextView
    private lateinit var updateButton: Button
    private lateinit var queryInput: EditText
    private lateinit var clearHistoryButton: Button
    private lateinit var historyHeader: TextView
    private lateinit var tracksScrollView: NestedScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: TrackSearchViewModel
    private lateinit var textWatcher: TextWatcher
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        initListeners()

        viewModel = ViewModelProvider(
            this, TrackSearchViewModel.getViewModelFactory()
        )[TrackSearchViewModel::class.java]

        if (savedInstanceState != null) {
            queryInput.setText(savedInstanceState.getString(SEARCH_PHRASE, ""))
        }

        tracksRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksRecyclerView.adapter = adapter
        historyInVisible()

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchPhrase = queryInput.text.toString()
                if (queryInput.hasFocus() && s?.isEmpty() == true && viewModel.getCurrentHistoryList()
                        .isNotEmpty()
                ) {
                    adapter.updateMediaAdapter(viewModel.getCurrentHistoryList())
                    historyVisible()
                } else {
                    historyInVisible()
                }

                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        queryInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && queryInput.text.isEmpty() && viewModel.getCurrentHistoryList()
                    .isNotEmpty()
            ) {
                adapter.updateMediaAdapter(viewModel.getCurrentHistoryList())
                historyVisible()
            } else {
                historyInVisible()
            }
        }
        textWatcher.let { queryInput.addTextChangedListener(it) }

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) {
            showToast(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { queryInput.removeTextChangedListener(it) }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showEmpty(state.message)
            is TracksState.Error -> showError(
                getString(R.string.st_no_internet),
                R.drawable.ic_search_no_internet,
                true,
                state.errorMessage
            )

            is TracksState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        tracksScrollView.visibility = GONE
        tracksRecyclerView.visibility = GONE
        placeHolderLayout.visibility = GONE
        progressBar.visibility = VISIBLE
    }

    /**
     *   функция динамического отображения плэйсхолдера и Toast при ошибках
     *   @property errorMessage - текст плэйсхолдера
     *   @property imgRes - номер изображения из ресурсов
     *   @property updBtn - видимость кнопки "Обновить" - true/false
     *   @property additionalMessage - дополнительное сообщение для отладки
     */
    private fun showError(
        errorMessage: String, imgRes: Int, updBtn: Boolean, additionalMessage: String
    ) {
        tracksScrollView.visibility = GONE
        tracksRecyclerView.visibility = GONE
        progressBar.visibility = GONE

        if (errorMessage.isNotEmpty()) {
            placeHolderLayout.visibility = VISIBLE
            placeHolderText.text = errorMessage
            placeHolderImage.setImageResource(imgRes)
            updateButton.visibility = when (updBtn) {
                true -> VISIBLE
                else -> GONE
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeHolderLayout.visibility = GONE
        }
    }

    private fun showEmpty(emptyMessage: String) {
        showError(
            getString(R.string.st_nothing_found), R.drawable.ic_search_no_item, false, emptyMessage
        )
    }

    private fun showContent(tracks: List<Track>) {
        tracksScrollView.visibility = VISIBLE
        tracksRecyclerView.visibility = VISIBLE
        placeHolderLayout.visibility = GONE
        progressBar.visibility = GONE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun initListeners() {

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            adapter.updateMediaAdapter(viewModel.getCurrentHistoryList())
            historyInVisible()
        }

        backButton.setOnClickListener {
            finish()
        }
        updateButton.setOnClickListener {
            viewModel.searchRequest(
                queryInput.text.toString()
            )
        }
        clearButton.setOnClickListener {
            placeHolderLayout.visibility = GONE
            queryInput.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(queryInput.windowToken, 0)
            queryInput.clearFocus()
        }


        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                historyInVisible()
                viewModel.searchRequest(
                    queryInput.text.toString()
                )
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
        queryInput = findViewById(R.id.searchEditText)
        backButton = findViewById(R.id.bt_search_back)
        clearButton = findViewById(R.id.clearIcon)
        historyHeader = findViewById(R.id.tvHistoryHeader)
        clearHistoryButton = findViewById(R.id.bt_clear_search_history)
        tracksScrollView = findViewById(R.id.nsvTracksInterfaceContainer)
        progressBar = findViewById(R.id.progressBar)
    }


    private fun historyVisible() {
        tracksScrollView.visibility = VISIBLE
        historyHeader.visibility = VISIBLE
        tracksRecyclerView.visibility = VISIBLE
        clearHistoryButton.visibility = VISIBLE
    }

    private fun historyInVisible() {
        tracksScrollView.visibility = GONE
        historyHeader.visibility = GONE
        tracksRecyclerView.visibility = GONE
        clearHistoryButton.visibility = GONE
    }

    private fun searchDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            handler.postDelayed({ isClickAllowed = true }, SEARCH_DEBOUNCE_DELAY)
            isClickAllowed = false
        }
        return current
    }

    companion object {
        const val SEARCH_PHRASE = "SEARCH_PHRASE"
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
    }
}