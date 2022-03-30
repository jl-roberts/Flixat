package com.jlroberts.flixat.ui.preferences

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.jlroberts.flixat.databinding.FragmentPreferencesBinding
import com.jlroberts.flixat.utils.THEME_AUTO
import com.jlroberts.flixat.utils.THEME_DARK
import com.jlroberts.flixat.utils.THEME_LIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreferencesFragment : Fragment() {

    private val viewModel by viewModels<PreferencesViewModel>()

    private var _binding: FragmentPreferencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreferencesBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupToolbar()
        setupListeners()
        setupObservers()

        return binding.root
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
    }

    private fun setupListeners() {
        binding.countryPreferenceLayout.setOnClickListener {
            launchClearCountryDialog()
        }
        binding.themePreferenceLayout.setOnClickListener {
            launchThemeDialog()
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.country.collect {
                    binding.countryPreferenceValue.text = it
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.theme.collect { theme ->
                        binding.themeSettingsValue.text = when (theme) {
                            Theme.ThemeLight -> "Light theme"
                            Theme.ThemeDark -> "Dark theme"
                            Theme.ThemeSystem -> "Follow system"
                        }
                    }
                }
            }
        }
    }

    private fun launchClearCountryDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Delete country setting?")
            .setPositiveButton("OK") { dialog, _ ->
                viewModel.clearCountry()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    private fun launchThemeDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Select a theme")
            .setItems(arrayOf("Light", "Dark", "System")) { dialog, which ->
                when (which) {
                    THEME_LIGHT -> {
                        viewModel.setTheme(Theme.ThemeLight)
                        dialog.dismiss()
                    }
                    THEME_DARK -> {
                        viewModel.setTheme(Theme.ThemeDark)
                        dialog.dismiss()
                    }
                    THEME_AUTO -> {
                        viewModel.setTheme(Theme.ThemeSystem)
                        dialog.dismiss()
                    }
                }
            }
            .create().show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}