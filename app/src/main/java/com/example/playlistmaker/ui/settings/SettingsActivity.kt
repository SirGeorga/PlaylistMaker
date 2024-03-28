package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.main.MainActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var shareButton: TextView
    private lateinit var supportButton: TextView
    private lateinit var agreementButton: TextView
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        initListeners()

    }

    private fun initViews() {
        themeSwitcher = findViewById(R.id.themeSwitcher)
        backButton = findViewById(R.id.bt_settings_back)
        shareButton = findViewById(R.id.bt_settings_share)
        supportButton = findViewById(R.id.bt_settings_support)
        agreementButton = findViewById(R.id.bt_settings_agreement)
    }

    private fun initListeners() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        themeSwitcher.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

            with(getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE).edit()) {
                putBoolean(MainActivity.THEME_PREF, checked)
                apply()
            }

        }
        backButton.setOnClickListener {
            finish()
        }
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.st_practicum_link))
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
            val browseIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.st_agreement_link)))
            startActivity(browseIntent)
        }
    }
}