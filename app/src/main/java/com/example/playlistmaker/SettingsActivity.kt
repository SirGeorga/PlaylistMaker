package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<TextView>(R.id.bt_settings_back)
        val shareButton = findViewById<TextView>(R.id.bt_settings_share)
        val supportButton = findViewById<TextView>(R.id.bt_settings_support)
        val agreementButton = findViewById<TextView>(R.id.bt_settings_agreement)

        backButton.setOnClickListener {
            finish()
        }
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.st_practicum_link))
            shareIntent.type = "text/html"
            startActivity(shareIntent)
        }
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.st_dev_email)))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.st_mail_text))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.st_mail_subject))
            startActivity(supportIntent)
        }
        agreementButton.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.st_agreement_link)))
            startActivity(browseIntent)
        }
    }
}