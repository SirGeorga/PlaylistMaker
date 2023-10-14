package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        val playerTrackName = findViewById<TextView>(R.id.playerTrackName)
        val playerArtistName = findViewById<TextView>(R.id.playerArtistName)
        val trackTime = findViewById<TextView>(R.id.time)
        val album = findViewById<TextView>(R.id.album)
        val year = findViewById<TextView>(R.id.year)
        val genre = findViewById<TextView>(R.id.genre)
        val country = findViewById<TextView>(R.id.country)
        val cover = findViewById<ImageView>(R.id.trackCover)

        val backButton = findViewById<TextView>(R.id.backArrow4)
        backButton.setOnClickListener {
            finish()
        }

        playerTrackName.text =
            intent.extras?.getString("Track Name") ?: getString(R.string.st_unknown_track)
        playerArtistName.text =
            intent.extras?.getString("Artist Name") ?: getString(R.string.st_unknown_artist)
        trackTime.text = intent.extras?.getString("Track Time") ?: getString(R.string.st_00_00)
        album.text = intent.extras?.getString("Album") ?: getString(R.string.st_unknown_album)
        year.text =
            (intent.extras?.getString("Year") ?: getString(R.string.st_unknown_year)).take(4)
        genre.text = intent.extras?.getString("Genre") ?: getString(R.string.st_unknown_genre)
        country.text = intent.extras?.getString("Country") ?: getString(R.string.st_unknown_country)
        val getImage = (intent.extras?.getString("Cover") ?: "Unknown Cover").replace(
            "100x100bb.jpg", "512x512bb.jpg"
        )
        if (getImage != "Unknown Cover") {
            getImage.replace("100x100bb.jpg", "512x512bb.jpg")
            Glide.with(this).load(getImage).placeholder(R.drawable.ic_album_placeholder).into(cover)
        }
    }
}