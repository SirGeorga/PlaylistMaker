package com.example.playlistmaker.mainscreen.ui.view_model

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.ui.activity.LibraryActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivityViewModel : ViewModel() {

    fun goToSettings(context: Context) {
        val displayIntent = Intent(context, SettingsActivity::class.java)
        context.startActivity(displayIntent)
    }

    fun goToLibrary(context: Context) {
        val displayIntent = Intent(context, LibraryActivity::class.java)
        context.startActivity(displayIntent)
    }

    fun goToSearch(context: Context) {
        val displayIntent = Intent(context, SearchActivity::class.java)
        context.startActivity(displayIntent)
    }
}