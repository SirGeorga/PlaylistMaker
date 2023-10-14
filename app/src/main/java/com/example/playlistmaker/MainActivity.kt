package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var settingsButton: Button
    private lateinit var libraryButton: Button
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = sharedPref.getBoolean(THEME_PREF, currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        (applicationContext as App).switchTheme(isDarkTheme)

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
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

        libraryButton.setOnClickListener {
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }

        searchButton.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }
    }

    companion object {
        const val PREFS = "my_prefs"
        const val THEME_PREF = "isDarkTheme"
    }
}