package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.themeSwitcher.isChecked = viewModel.getTheme()
        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)
        }

        binding.btSettingsShare.setOnClickListener {
            viewModel.shareApp()
        }
        binding.btSettingsSupport.setOnClickListener {
            viewModel.openSupport()
        }
        binding.btSettingsAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}