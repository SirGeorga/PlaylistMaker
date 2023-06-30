package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.networkModule.ITunesApi
import com.example.playlistmaker.networkModule.SongsSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_PHRASE = "SEARCH_PHRASE"
    }

    private var searchPhrase: String = ""
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_PHRASE, searchPhrase)
    }

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit =
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()

    private lateinit var placeHolderLayout: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var backButton: TextView
    private lateinit var clearButton: ImageView
    private lateinit var placeHolderText: TextView
    private lateinit var updateButton: Button
    private lateinit var searchEditText: EditText
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private val mediaAdapter = MediaAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val tracksRecyclerView = findViewById<RecyclerView>(R.id.tracksRecyclerView)

        initViews()
        initListeners()

        mediaAdapter.tracks = tracks
        tracksRecyclerView.adapter = mediaAdapter

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
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun initListeners() {

        backButton.setOnClickListener {
            finish()
        }
        clearButton.setOnClickListener {
            searchEditText.setText("")
            tracks.clear()
            mediaAdapter.notifyDataSetChanged()
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
                searchQuery()
                true
            }
            false
        }
    }

    private fun initViews() {
        placeHolderLayout = findViewById(R.id.llSearchPlaceholder)
        placeHolderImage = findViewById(R.id.ivSearchPlaceholderImg)
        placeHolderText = findViewById(R.id.tvSearchPlaceholderTxt)
        updateButton = findViewById(R.id.btSearchUpdate)
        searchEditText = findViewById(R.id.searchEditText)
        backButton = findViewById(R.id.bt_search_back)
        clearButton = findViewById(R.id.clearIcon)
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
                                tracks.addAll(response.body()?.results!!)
                                mediaAdapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
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

    private fun showMessage(text: String, imgRes: Int, updBtn: Boolean, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeHolderLayout.visibility = View.VISIBLE
            tracks.clear()
            mediaAdapter.notifyDataSetChanged()
            placeHolderText.text = text
            placeHolderImage.setImageResource(imgRes)
            if (updBtn) updateButton.visibility = View.VISIBLE
            else updateButton.visibility = View.GONE
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            placeHolderLayout.visibility = View.GONE
        }
    }
}