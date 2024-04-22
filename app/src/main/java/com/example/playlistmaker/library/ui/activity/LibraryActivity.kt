package com.example.playlistmaker.library.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.example.playlistmaker.library.ui.fragments.LibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        binding.viewPager.adapter = LibraryViewPagerAdapter(
            supportFragmentManager, lifecycle
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.st_tab_favourites)
                1 -> tab.text = getString(R.string.st_tab_playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}