package com.example.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.data.TracksState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.recycler_view.TracksAdapter
import com.example.playlistmaker.search.ui.view_model.TrackSearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private var searchPhrase: String = ""
    private val viewModel: TrackSearchViewModel by viewModel()
    private lateinit var textWatcher: TextWatcher
    private var isClickAllowed = true
    private lateinit var binding: FragmentSearchBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    private val adapter = TracksAdapter(object : TracksAdapter.TrackClickListener {
        override fun onTrackClick(track: Track) {
            if (searchDebounce()) {
                viewModel.addTrackToHistory(track)
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("track", track)
                startActivity(intent)
            }
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        if (savedInstanceState != null) {
            binding.searchEditText.setText(savedInstanceState.getString(SEARCH_PHRASE, ""))
        }

        binding.tracksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksRecyclerView.adapter = adapter
        historyInVisible()

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                searchPhrase = binding.searchEditText.text.toString()
                if (binding.searchEditText.hasFocus() && s?.isEmpty() == true && viewModel.getCurrentHistoryList()
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

        binding.searchEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty() && viewModel.getCurrentHistoryList()
                    .isNotEmpty()
            ) {
                adapter.updateMediaAdapter(viewModel.getCurrentHistoryList())
                historyVisible()
            } else {
                historyInVisible()
            }
        }
        textWatcher.let { binding.searchEditText.addTextChangedListener(it) }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_PHRASE, searchPhrase)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { binding.searchEditText.removeTextChangedListener(it) }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
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
        binding.nsvTracksInterfaceContainer.visibility = GONE
        binding.tracksRecyclerView.visibility = GONE
        binding.llSearchPlaceholder.visibility = GONE
        binding.progressBar.visibility = VISIBLE
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
        binding.nsvTracksInterfaceContainer.visibility = GONE
        binding.tracksRecyclerView.visibility = GONE
        binding.progressBar.visibility = GONE

        if (errorMessage.isNotEmpty()) {
            binding.llSearchPlaceholder.visibility = VISIBLE
            binding.tvSearchPlaceholderTxt.text = errorMessage
            binding.ivSearchPlaceholderImg.setImageResource(imgRes)
            binding.btSearchUpdate.visibility = when (updBtn) {
                true -> VISIBLE
                else -> GONE
            }
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
            }
        } else {
            binding.llSearchPlaceholder.visibility = GONE
        }
    }

    private fun showEmpty(emptyMessage: String) {
        showError(
            getString(R.string.st_nothing_found), R.drawable.ic_search_no_item, false, emptyMessage
        )
    }

    private fun showContent(tracks: List<Track>) {
        binding.nsvTracksInterfaceContainer.visibility = VISIBLE
        binding.tracksRecyclerView.visibility = VISIBLE
        binding.llSearchPlaceholder.visibility = GONE
        binding.progressBar.visibility = GONE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun initListeners() {

        binding.btClearSearchHistory.setOnClickListener {
            viewModel.clearHistory()
            adapter.updateMediaAdapter(viewModel.getCurrentHistoryList())
            historyInVisible()
        }

        binding.btSearchUpdate.setOnClickListener {
            viewModel.searchRequest(
                binding.searchEditText.text.toString()
            )
        }
        binding.clearIcon.setOnClickListener {
            binding.llSearchPlaceholder.visibility = GONE
            binding.searchEditText.setText("")
            val keyboard =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
            binding.searchEditText.clearFocus()
        }


        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                historyInVisible()
                viewModel.searchRequest(
                    binding.searchEditText.text.toString()
                )
                true
            }
            false
        }
    }

    private fun historyVisible() {
        binding.nsvTracksInterfaceContainer.visibility = VISIBLE
        binding.tvHistoryHeader.visibility = VISIBLE
        binding.tracksRecyclerView.visibility = VISIBLE
        binding.btClearSearchHistory.visibility = VISIBLE
    }

    private fun historyInVisible() {
        binding.nsvTracksInterfaceContainer.visibility = GONE
        binding.tvHistoryHeader.visibility = GONE
        binding.tracksRecyclerView.visibility = GONE
        binding.btClearSearchHistory.visibility = GONE
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