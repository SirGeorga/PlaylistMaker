package com.example.playlistmaker.mainscreen.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.mainscreen.ui.view_model.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var settingsButton: Button
    private lateinit var libraryButton: Button
    private lateinit var searchButton: Button
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        initViews()
        initListeners()
    }

    private fun initViews() {
        settingsButton = findViewById(R.id.bt_main_settings)
        libraryButton = findViewById(R.id.bt_main_library)
        searchButton = findViewById(R.id.bt_main_search)
    }

    private fun initListeners() {
        settingsButton.setOnClickListener {
            viewModel.goToSettings(this)
        }

        libraryButton.setOnClickListener {
            viewModel.goToLibrary(this)
        }

        searchButton.setOnClickListener {
            viewModel.goToSearch(this)
        }
    }
}