package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var shareButton: TextView
    private lateinit var supportButton: TextView
    private lateinit var agreementButton: TextView
    private lateinit var themeSwitcher: SwitchMaterial
    private val viewModel: SettingsViewModel by viewModel()

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
        themeSwitcher.isChecked = viewModel.getTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)
        }

        backButton.setOnClickListener {
            finish()
        }
        shareButton.setOnClickListener {
            viewModel.shareApp()
        }
        supportButton.setOnClickListener {
            viewModel.openSupport()
        }
        agreementButton.setOnClickListener {
            viewModel.openTerms()
        }
    }
}