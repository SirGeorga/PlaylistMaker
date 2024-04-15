package com.example.playlistmaker.mainscreen.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.mainscreen.ui.view_model.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var settingsButton: Button
    private lateinit var libraryButton: Button
    private lateinit var searchButton: Button
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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